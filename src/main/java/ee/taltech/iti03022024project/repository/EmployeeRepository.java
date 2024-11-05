package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    boolean existsByNameIgnoreCase(String name);

    // ei tea miks ei tööta
    //String getPasswordByName(String name);

    @Query("SELECT e.password FROM EmployeeEntity e WHERE e.name = :name")
    String getPasswordByName(@Param("name") String name);


    EmployeeEntity getByNameIgnoreCase(String name);
    boolean existsByName(String name);
}
