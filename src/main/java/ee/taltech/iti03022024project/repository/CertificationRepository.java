package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.CertificationEntity;
import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationRepository extends JpaRepository<CertificationEntity, Integer> {
    boolean existsByEmployeeAndCertificationType(EmployeeEntity employee, CertificationTypeEntity certificationType);
}
