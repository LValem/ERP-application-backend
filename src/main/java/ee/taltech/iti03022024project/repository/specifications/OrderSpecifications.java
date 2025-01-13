package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecifications {
    private static final String WEIGHT = "weight";
    private static final String LENGTH = "length";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String DROP_OFF_DATE = "dropOffDate";

    private OrderSpecifications() {}

    public static Specification<OrderEntity> orderId(Integer orderId) {
        return (root, query, cb) -> orderId == null ? null : cb.equal(root.get("orderId"), orderId);
    }

    public static Specification<OrderEntity> customerNameLike(String customerName) {
        return (root, query, cb) -> {
            if (customerName == null || customerName.isEmpty()) return null;
            return cb.like(cb.lower(root.get("customer").get("name")), "%" + customerName.toLowerCase() + "%");
        };
    }

    public static Specification<OrderEntity> pickupDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null || end == null) return null;
            return cb.between(root.get("pickupDate"), start, end);
        };
    }

    public static Specification<OrderEntity> dropOffDateBetween(LocalDateTime start, LocalDateTime end) {
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

    public static Specification<OrderEntity> weightBetween(Integer minWeight, Integer maxWeight) {
        return (root, query, cb) -> {
            if (minWeight == null && maxWeight == null) return null;
            if (minWeight != null && maxWeight != null) {
                return cb.between(root.get(WEIGHT), minWeight, maxWeight);
            } else if (minWeight != null) {
                return cb.greaterThanOrEqualTo(root.get(WEIGHT), minWeight);
            } else {
                return cb.lessThanOrEqualTo(root.get(WEIGHT), maxWeight);
            }
        };
    }

    public static Specification<OrderEntity> lengthBetween(Integer minLength, Integer maxLength) {
        return (root, query, cb) -> {
            if (minLength == null && maxLength == null) return null;
            if (minLength != null && maxLength != null) {
                return cb.between(root.get(LENGTH), minLength, maxLength);
            } else if (minLength != null) {
                return cb.greaterThanOrEqualTo(root.get(LENGTH), minLength);
            } else {
                return cb.lessThanOrEqualTo(root.get(LENGTH), maxLength);
            }
        };
    }

    public static Specification<OrderEntity> widthBetween(Integer minWidth, Integer maxWidth) {
        return (root, query, cb) -> {
            if (minWidth == null && maxWidth == null) return null;
            if (minWidth != null && maxWidth != null) {
                return cb.between(root.get(WIDTH), minWidth, maxWidth);
            } else if (minWidth != null) {
                return cb.greaterThanOrEqualTo(root.get(WIDTH), minWidth);
            } else {
                return cb.lessThanOrEqualTo(root.get(WIDTH), maxWidth);
            }
        };
    }

    public static Specification<OrderEntity> heightBetween(Integer minHeight, Integer maxHeight) {
        return (root, query, cb) -> {
            if (minHeight == null && maxHeight == null) return null;
            if (minHeight != null && maxHeight != null) {
                return cb.between(root.get(HEIGHT), minHeight, maxHeight);
            } else if (minHeight != null) {
                return cb.greaterThanOrEqualTo(root.get(HEIGHT), minHeight);
            } else {
                return cb.lessThanOrEqualTo(root.get(HEIGHT), maxHeight);
            }
        };
    }
}
