package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.VehicleEntity;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class VehicleSpecifications {

    private static final String VEHICLE_TYPE = "vehicleType";
    private static final String REGISTRATION_PLATE = "registrationPlate";

    private VehicleSpecifications() {}

    public static Specification<VehicleEntity> vehicleId(Integer vehicleId) {
        return (root, query, cb) -> vehicleId == null ? null : cb.equal(root.get("vehicleId"), vehicleId);
    }

    public static Specification<VehicleEntity> vehicleType(Character vehicleType) {
        return (root, query, cb) -> vehicleType == null ? null : cb.equal(root.get(VEHICLE_TYPE), vehicleType);
    }

    public static Specification<VehicleEntity> isInUse(Boolean isInUse) {
        return (root, query, cb) -> isInUse == null ? null : cb.equal(root.get("isInUse"), isInUse);
    }

    public static Specification<VehicleEntity> loadBetween(Integer minimumLoad, Integer maximumLoad) {
        return (root, query, cb) -> {
            if (minimumLoad == null && maximumLoad == null) return null;
            if (minimumLoad != null && maximumLoad != null) {
                return cb.between(root.get("maxLoad"), minimumLoad, maximumLoad);
            } else if (minimumLoad != null) {
                return cb.greaterThanOrEqualTo(root.get("maxLoad"), minimumLoad);
            } else {
                return cb.lessThanOrEqualTo(root.get("maxLoad"), maximumLoad);
            }
        };
    }

    public static Specification<VehicleEntity> fuelBetween(Integer minFuel, Integer maxFuel) {
        return (root, query, cb) -> {
            if (minFuel == null && maxFuel == null) return null;
            if (minFuel != null && maxFuel != null) {
                return cb.between(root.get("currentFuel"), minFuel, maxFuel);
            } else if (minFuel != null) {
                return cb.greaterThanOrEqualTo(root.get("currentFuel"), minFuel);
            } else {
                return cb.lessThanOrEqualTo(root.get("currentFuel"), maxFuel);
            }
        };
    }


    public static Specification<VehicleEntity> registrationPlateLike(String registrationPlate) {
        return (root, query, cb) -> {
            if (registrationPlate == null || registrationPlate.isEmpty()) return null;
            return cb.like(cb.lower(root.get(REGISTRATION_PLATE)), "%" + registrationPlate.toLowerCase() + "%");
        };
    }
}
