package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Integer> {
    boolean existsByRegistrationPlate(String registrationPlate);
}
