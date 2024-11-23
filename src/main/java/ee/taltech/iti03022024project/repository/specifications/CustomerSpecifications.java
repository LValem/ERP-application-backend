package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.CustomerEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CustomerSpecifications {

    private static final String LAST_ORDER_DATE = "lastOrderDate";

    public static Specification<CustomerEntity> customerId(Integer customerId) {
        return (root, query, cb) -> customerId == null ? null : cb.equal(root.get("customerId"), customerId);
    }

    public static Specification<CustomerEntity> customerNameLike(String customerName) {
        return (root, query, cb) -> {
            if (customerName == null || customerName.isEmpty()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + customerName.toLowerCase() + "%");
        };
    }

    public static Specification<CustomerEntity> addressLike(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isEmpty()) return null;
            return cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%");
        };
    }

    public static Specification<CustomerEntity> cityCountyLike(String cityCounty) {
        return (root, query, cb) -> {
            if (cityCounty == null || cityCounty.isEmpty()) return null;
            return cb.like(cb.lower(root.get("cityCounty")), "%" + cityCounty.toLowerCase() + "%");
        };
    }

    public static Specification<CustomerEntity> zip(String zip) {
        return (root, query, cb) -> zip == null ? null : cb.equal(root.get("zip"), zip);
    }

    public static Specification<CustomerEntity> emailLike(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) return null;
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<CustomerEntity> phoneNumber(String phoneNumber) {
        return (root, query, cb) -> phoneNumber == null ? null : cb.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<CustomerEntity> vatNo(String vatNo) {
        return (root, query, cb) -> vatNo == null ? null : cb.equal(root.get("vatNo"), vatNo);
    }

    public static Specification<CustomerEntity> lastOrderDateBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null) {
                return cb.between(root.get(LAST_ORDER_DATE), start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(root.get(LAST_ORDER_DATE), start);
            } else {
                return cb.lessThanOrEqualTo(root.get(LAST_ORDER_DATE), end);
            }
        };
    }
}
