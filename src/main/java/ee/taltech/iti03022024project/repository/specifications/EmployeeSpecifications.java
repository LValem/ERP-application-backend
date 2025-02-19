package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.*;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EmployeeSpecifications {

    private static final String DESCRIPTION = "description";
    private static final String EMPLOYEE = "employee";

    private EmployeeSpecifications() {}

    public static Specification<EmployeeEntity> employeeId(Integer employeeId) {
        return (root, query, cb) -> employeeId == null ? null : cb.equal(root.get("employeeId"), employeeId);
    }

    public static Specification<EmployeeEntity> employeeNameLike(String employeeName) {
        return (root, query, cb) -> {
            if (employeeName == null || employeeName.isEmpty()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + employeeName.toLowerCase() + "%");
        };
    }

    public static Specification<EmployeeEntity> permissionDescription(String permissionDescription) {
        return (root, query, cb) -> {
            if (permissionDescription == null || permissionDescription.isEmpty()) return null;
            return cb.like(cb.lower(root.join("permission").get(DESCRIPTION)), "%" + permissionDescription.toLowerCase() + "%");
        };
    }

    public static Specification<EmployeeEntity> sortByPermissionDescription(Sort.Direction direction) {
        return (root, query, cb) -> {
            var permissionJoin = root.join("permission");

            assert query != null;
            if (direction == Sort.Direction.DESC) {
                query.orderBy(cb.desc(permissionJoin.get(DESCRIPTION)));
            } else {
                query.orderBy(cb.asc(permissionJoin.get(DESCRIPTION)));
            }
            return null;
        };
    }

    public static Specification<EmployeeEntity> certificationNamesLike(String certificationNames) {
        return (root, query, cb) -> {
            if (certificationNames == null || certificationNames.isEmpty()) return null;

            Join<EmployeeEntity, CertificationEntity> certificationJoin = root.join("certifications");
            Join<CertificationEntity, CertificationTypeEntity> certificationTypeJoin = certificationJoin.join("certificationType");

            return cb.like(cb.lower(certificationTypeJoin.get("certificationName")), "%" + certificationNames.toLowerCase() + "%");
        };
    }

    public static Specification<EmployeeEntity> sortByCertificationNames(Sort.Direction direction) {
        return (root, query, cb) -> {
            assert query != null;
            Subquery<String> subquery = query.subquery(String.class);
            Root<CertificationEntity> certificationRoot = subquery.from(CertificationEntity.class);

            Expression<String> concatenatedCertifications = cb.function(
                    "STRING_AGG", String.class,
                    certificationRoot.join("certificationType").get("certificationName"),
                    cb.literal(", ")
            );

            subquery.select(concatenatedCertifications)
                    .where(cb.equal(certificationRoot.get(EMPLOYEE), root));

            if (direction == Sort.Direction.DESC) {
                query.orderBy(cb.desc(subquery));
            } else {
                query.orderBy(cb.asc(subquery));
            }
            return null;
        };
    }

    public static Specification<EmployeeEntity> lastJobDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            assert query != null;
            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<JobEntity> jobRoot = subquery.from(JobEntity.class);

            subquery.select(cb.greatest(jobRoot.<LocalDateTime>get("dropOffDate")))
                    .where(cb.equal(jobRoot.get(EMPLOYEE), root));

            if (startDate != null && endDate != null) {
                return cb.between(subquery, startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(subquery, startDate);
            } else if (endDate != null) {
                return cb.lessThanOrEqualTo(subquery, endDate);
            } else {
                return null;
            }
        };
    }

    public static Specification<EmployeeEntity> sortByLastJobDate(Sort.Direction direction) {
        return (root, query, cb) -> {
            assert query != null;
            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<JobEntity> jobRoot = subquery.from(JobEntity.class);

            subquery.select(cb.greatest(jobRoot.<LocalDateTime>get("dropOffDate")))
                    .where(cb.equal(jobRoot.get(EMPLOYEE), root));

            if (direction == Sort.Direction.DESC) {
                query.orderBy(cb.desc(subquery));
            } else {
                query.orderBy(cb.asc(subquery));
            }
            return null;
        };
    }
}
