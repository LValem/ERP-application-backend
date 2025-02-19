package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer>, JpaSpecificationExecutor<EmployeeEntity> {
    boolean existsByNameIgnoreCase(String name);
    Optional<EmployeeEntity> getByNameIgnoreCase(String name);
}
