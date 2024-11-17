package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.JobEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class DoneJobSpecifications {

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
            if (start == null || end == null) return null;
            return cb.between(root.get("pickupDate"), start, end);
        };
    }

    public static Specification<JobEntity> dropOffDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get("dropOffDate"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("dropOffDate"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("dropOffDate"), end);
            }
        };
    }

    public static Specification<JobEntity> fuelUsedBetween(Double minFuelUsed, Double maxFuelUsed) {
        return (root, query, cb) -> {
            if (minFuelUsed == null && maxFuelUsed == null) return null;
            if (minFuelUsed != null && maxFuelUsed != null) {
                return cb.between(root.join("fuelConsumption").get("fuelUsed"), minFuelUsed, maxFuelUsed);
            } else if (minFuelUsed != null) {
                return cb.greaterThanOrEqualTo(root.join("fuelConsumption").get("fuelUsed"), minFuelUsed);
            } else {
                return cb.lessThanOrEqualTo(root.join("fuelConsumption").get("fuelUsed"), maxFuelUsed);
            }
        };
    }
}
