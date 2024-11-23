package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.EmployeeEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class EmployeeSpecifications {
    private static final String DROPOFF_DATE = "dropOffDate";

    public static Specification<EmployeeEntity> employeeId(Integer employeeId) {
        return (root, query, cb) -> employeeId == null ? null : cb.equal(root.get("employeeId"), employeeId);
    }

    public static Specification<EmployeeEntity> employeeNameLike(String employeeName) {
        return (root, query, cb) -> {
            if (employeeName == null || employeeName.isEmpty()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + employeeName.toLowerCase() + "%");
        };
    }

    public static Specification<EmployeeEntity> permissionDescriptionLike(String permissionDescription) {
        return (root, query, cb) -> {
            if (permissionDescription == null || permissionDescription.isEmpty()) return null;
            return cb.like(cb.lower(root.get("permission").get("description")), "%" + permissionDescription.toLowerCase() + "%");
        };
    }

    public static Specification<EmployeeEntity> certificationNameLike(String certificationNames) {
        return (root, query, cb) -> {
            if (certificationNames == null || certificationNames.isEmpty()) return null;
            return cb.like(cb.lower(root.join("certifications").join("certificationType").get("certificationName")),
                    "%" + certificationNames.toLowerCase() + "%");
        };
    }

    public static Specification<EmployeeEntity> lastJobDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, cb) -> {
            if (startDate == null && endDate == null) return null;
            if (startDate != null && endDate != null) {
                return cb.between(root.join("jobs").get(DROPOFF_DATE), startDate, endDate);
            } else if (startDate != null) {
                return cb.greaterThanOrEqualTo(root.join("jobs").get(DROPOFF_DATE), startDate);
            } else {
                return cb.lessThanOrEqualTo(root.join("jobs").get(DROPOFF_DATE), endDate);
            }
        };
    }
}
