package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.JobEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotDoneJobSpecifications {

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
                return cb.between(root.get("order").get("pickupDate"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("order").get("pickupDate"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("order").get("pickupDate"), end);
            }
        };
    }

    public static Specification<JobEntity> dropOffDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get("order").get("dropOffDate"), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("order").get("dropOffDate"), start);
            } else {
                return cb.lessThanOrEqualTo(root.get("order").get("dropOffDate"), end);
            }
        };
    }

    public static Specification<JobEntity> isComplete() {
        return (root, query, cb) -> cb.isFalse(root.get("isComplete"));
    }
}
