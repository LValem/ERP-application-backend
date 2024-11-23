package ee.taltech.iti03022024project.repository.specifications;

import ee.taltech.iti03022024project.entity.CustomerEntity;
import ee.taltech.iti03022024project.entity.OrderEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CustomerSpecifications {

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
            assert query != null;
            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<OrderEntity> orderRoot = subquery.from(OrderEntity.class);

            subquery.select(cb.greatest(orderRoot.<LocalDateTime>get("dropOffDate")))
                    .where(cb.equal(orderRoot.get("customer").get("customerId"), root.get("customerId")));

            if (start != null && end != null) {
                return cb.between(subquery, start, end);
            } else if (start != null) {
                return cb.greaterThanOrEqualTo(subquery, start);
            } else if (end != null) {
                return cb.lessThanOrEqualTo(subquery, end);
            } else {
                return null;
            }
        };
    }

    public static Specification<CustomerEntity> sortByLastOrderDate(Sort.Direction direction) {
        return (root, query, cb) -> {
            assert query != null;
            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<OrderEntity> orderRoot = subquery.from(OrderEntity.class);

            subquery.select(cb.greatest(orderRoot.<LocalDateTime>get("dropOffDate")))
                    .where(cb.equal(orderRoot.get("customer").get("customerId"), root.get("customerId")));

            if (direction == Sort.Direction.DESC) {
                query.orderBy(cb.desc(subquery));
            } else {
                query.orderBy(cb.asc(subquery));
            }

            return null;
        };
    }
}
