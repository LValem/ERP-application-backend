package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Integer>, JpaSpecificationExecutor<JobEntity> {
}
