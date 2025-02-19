package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Integer>{
}
