package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.security.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Integer> {
    boolean existsByDescriptionIgnoreCase(String description);
}
