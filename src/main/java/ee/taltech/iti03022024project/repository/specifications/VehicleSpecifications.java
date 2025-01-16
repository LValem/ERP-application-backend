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

    public static Specification<VehicleEntity> maxLoad(Integer maxLoad) {
        return (root, query, cb) -> maxLoad == null ? null : cb.equal(root.get("maxLoad"), maxLoad);
    }

    public static Specification<VehicleEntity> currentFuel(Integer currentFuel) {
        return (root, query, cb) -> currentFuel == null ? null : cb.equal(root.get("currentFuel"), currentFuel);
    }

    public static Specification<VehicleEntity> registrationPlateLike(String registrationPlate) {
        return (root, query, cb) -> {
            if (registrationPlate == null || registrationPlate.isEmpty()) return null;
            return cb.like(cb.lower(root.get(REGISTRATION_PLATE)), "%" + registrationPlate.toLowerCase() + "%");
        };
    }

//    public static Specification<VehicleEntity> sortByRegistrationPlate(Sort.Direction direction) {
//        return (root, query, cb) -> {
//            if (query != null) {
//                if (direction == Sort.Direction.DESC) {
//                    query.orderBy(cb.desc(root.get(REGISTRATION_PLATE)));
//                } else {
//                    query.orderBy(cb.asc(root.get(REGISTRATION_PLATE)));
//                }
//            }
//            return null;
//        };
//    }
}
