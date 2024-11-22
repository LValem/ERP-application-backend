package ee.taltech.iti03022024project.repository;

import ee.taltech.iti03022024project.dto.query.CustomerTableInfoDto;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


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
            MAX(o.dropOffDate)
        )
        FROM CustomerEntity c
        LEFT JOIN OrderEntity o ON o.customer = c
        GROUP BY c.customerId, c.name, c.address, c.cityCounty, c.zip, c.email, c.phoneNumber, c.vatNo
    """)
    Page<CustomerTableInfoDto> getCustomerTableInfo(Specification<CustomerEntity> spec, Pageable pageable);
}
