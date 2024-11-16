package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    boolean existsByNameIgnoreCase(String name);
    Optional<EmployeeEntity> getByNameIgnoreCase(String name);

    @Query("""
    SELECT new ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto(
        e.employeeId,
        e.name,
        p.description,
        cts.certificationName,
        MAX(j.dropOffDate)
    )
    FROM EmployeeEntity e
    LEFT JOIN PermissionEntity p ON p.permissionId = e.permissionId
    LEFT JOIN e.certifications c
    LEFT JOIN c.certificationType cts
    LEFT JOIN JobEntity j ON j.employee = e
    GROUP BY e.name, p.description, cts.certificationName
""")
    List<EmployeeTableInfoDto> getEmployeeTableInfo();
}
