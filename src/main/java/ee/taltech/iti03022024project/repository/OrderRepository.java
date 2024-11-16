package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.dto.query.OrdersTableInfoDto;
import ee.taltech.iti03022024project.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer>, JpaSpecificationExecutor<OrderEntity> {

    @Query("""
    SELECT new ee.taltech.iti03022024project.dto.query.OrdersTableInfoDto(
        o.orderId,
        c.name,
        o.pickupDate,
        o.dropOffDate,
        o.weight,
        o.width,
        o.height,
        o.length,
        o.orderDetails
    )
    FROM OrderEntity o
    JOIN o.customer c
    """)
    List<OrdersTableInfoDto> getOrdersTableInfo();
}
