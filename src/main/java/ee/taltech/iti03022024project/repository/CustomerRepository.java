package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.dto.query.CustomerTableInfoDto;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer>, JpaSpecificationExecutor<CustomerEntity> {
    boolean existsByNameIgnoreCase(String name);

    @Query("""
        SELECT new ee.taltech.iti03022024project.dto.query.CustomerTableInfoDto(
            c.customerId,
            c.name,
            c.address,
            c.cityCounty,
            c.zip,
            c.email,
            c.phoneNumber,
            c.vatNo,
            (SELECT MAX(o.dropOffDate) FROM OrderEntity o WHERE o.customer = c)
        )
        FROM CustomerEntity c
    """)
    List<CustomerTableInfoDto> getCustomerTableInfo();
}
