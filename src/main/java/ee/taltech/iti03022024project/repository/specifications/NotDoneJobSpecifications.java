package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.JobEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class NotDoneJobSpecifications {

    private static final String DROP_OFF_DATE = "dropOffDate";
    private static final String PICK_UP_DATE = "pickupDate";
    private static final String ORDER = "order";

    private NotDoneJobSpecifications() {}

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
        return (root, query, cb) -> orderId == null ? null : cb.equal(root.get(ORDER).get("orderId"), orderId);
    }

    public static Specification<JobEntity> customerNameLike(String customerName) {
        return (root, query, cb) -> {
            if (customerName == null || customerName.isEmpty()) return null;
            return cb.like(cb.lower(root.get(ORDER).get("customer").get("name")), "%" + customerName.toLowerCase() + "%");
        };
    }

    public static Specification<JobEntity> pickupDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get(ORDER).get(PICK_UP_DATE), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get(ORDER).get(PICK_UP_DATE), start);
            } else {
                return cb.lessThanOrEqualTo(root.get(ORDER).get(PICK_UP_DATE), end);
            }
        };
    }

    public static Specification<JobEntity> dropOffDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get(ORDER).get(DROP_OFF_DATE), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get(ORDER).get(DROP_OFF_DATE), start);
            } else {
                return cb.lessThanOrEqualTo(root.get(ORDER).get(DROP_OFF_DATE), end);
            }
        };
    }

    public static Specification<JobEntity> isComplete() {
        return (root, query, cb) -> cb.isFalse(root.get("isComplete"));
    }
}
