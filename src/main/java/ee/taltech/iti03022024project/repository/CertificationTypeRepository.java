package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificationTypeRepository extends JpaRepository<CertificationTypeEntity, Integer>, JpaSpecificationExecutor<CertificationTypeEntity> {
    boolean existsByCertificationNameIgnoreCase(String certificationName);
}
