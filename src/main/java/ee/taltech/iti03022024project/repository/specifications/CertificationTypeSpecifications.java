package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import org.springframework.data.jpa.domain.Specification;

public class CertificationTypeSpecifications {

    private CertificationTypeSpecifications() {}

    public static Specification<CertificationTypeEntity> certificationTypeId(Integer certificationTypeId) {
        return (root, query, cb) -> certificationTypeId == null ? null : cb.equal(root.get("certificationTypeId"), certificationTypeId);
    }

    public static Specification<CertificationTypeEntity> certificationNameLike(String certificationName) {
        return (root, query, cb) -> {
            if (certificationName == null || certificationName.isEmpty()) return null;
            return cb.like(cb.lower(root.get("certificationName")), "%" + certificationName.toLowerCase() + "%");
        };
    }
}
