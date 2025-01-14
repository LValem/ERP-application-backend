package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.JobEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class DoneJobSpecifications {
    private static final String PICKUP_DATE = "pickupDate";
    private static final String DROP_OFF_DATE = "dropOffDate";
    private static final String FUEL_CONSUMPTION = "fuelConsumption";
    private static final String FUEL_USED = "fuelUsed";
    private static final String DISTANCE_DRIVEN = "distanceDriven";

    private DoneJobSpecifications() {}

    public static Specification<JobEntity> jobId(Integer jobId) {
        return (root, query, cb) -> jobId == null ? null : cb.equal(root.get("jobId"), jobId);
    }

    public static Specification<JobEntity> vehicleId(Integer vehicleId) {
        return (root, query, cb) -> vehicleId == null ? null : cb.equal(root.get("vehicle").get("vehicleId"), vehicleId);
    }

    public static Specification<JobEntity> registrationPlateLike(String registrationPlate) {
        return (root, query, cb) -> {
            if (registrationPlate == null || registrationPlate.isEmpty()) return null;
            return cb.like(cb.lower(root.get("vehicle").get("registrationPlate")), "%" + registrationPlate.toLowerCase() + "%");
        };
    }

    public static Specification<JobEntity> orderId(Integer orderId) {
        return (root, query, cb) -> orderId == null ? null : cb.equal(root.get("order").get("orderId"), orderId);
    }

    public static Specification<JobEntity> customerNameLike(String customerName) {
        return (root, query, cb) -> {
            if (customerName == null || customerName.isEmpty()) return null;
            return cb.like(cb.lower(root.get("order").get("customer").get("name")), "%" + customerName.toLowerCase() + "%");
        };
    }

    public static Specification<JobEntity> pickupDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get(PICKUP_DATE), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get(PICKUP_DATE), start);
            } else {
                return cb.lessThanOrEqualTo(root.get(PICKUP_DATE), end);
            }
        };
    }

    public static Specification<JobEntity> dropOffDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get(DROP_OFF_DATE), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get(DROP_OFF_DATE), start);
            } else {
                return cb.lessThanOrEqualTo(root.get(DROP_OFF_DATE), end);
            }
        };
    }

    public static Specification<JobEntity> fuelUsedBetween(Double minFuelUsed, Double maxFuelUsed) {
        return (root, query, cb) -> {
            if (minFuelUsed == null && maxFuelUsed == null) return null;
            if (minFuelUsed != null && maxFuelUsed != null) {
                return cb.between(root.join(FUEL_CONSUMPTION).get(FUEL_USED), minFuelUsed, maxFuelUsed);
            } else if (minFuelUsed != null) {
                return cb.greaterThanOrEqualTo(root.join(FUEL_CONSUMPTION).get(FUEL_USED), minFuelUsed);
            } else {
                return cb.lessThanOrEqualTo(root.join(FUEL_CONSUMPTION).get(FUEL_USED), maxFuelUsed);
            }
        };
    }

    public static Specification<JobEntity> distanceDrivenBetween(Double minDistanceDriven, Double maxDistanceDriven) {
        return (root, query, cb) -> {
            if (minDistanceDriven == null && maxDistanceDriven == null) return null;
            if (minDistanceDriven != null && maxDistanceDriven != null) {
                return cb.between(root.join(FUEL_CONSUMPTION).get(DISTANCE_DRIVEN), minDistanceDriven, maxDistanceDriven);
            } else if (minDistanceDriven != null) {
                return cb.greaterThanOrEqualTo(root.join(FUEL_CONSUMPTION).get(DISTANCE_DRIVEN), minDistanceDriven);
            } else {
                return cb.lessThanOrEqualTo(root.join(FUEL_CONSUMPTION).get(DISTANCE_DRIVEN), maxDistanceDriven);
            }
        };
    }

    public static Specification<JobEntity> isComplete() {
        return (root, query, cb) -> cb.isTrue(root.get("isComplete"));
    }
}

