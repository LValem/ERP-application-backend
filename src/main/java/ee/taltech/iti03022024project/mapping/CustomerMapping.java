package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.CustomerDto;
import ee.taltech.iti03022024project.dto.query.CustomerTableInfoDto;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import ee.taltech.iti03022024project.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapping {
    CustomerDto customerToDto(CustomerEntity customerEntity);
    CustomerEntity customerToEntity(CustomerDto customerDto);
    List<CustomerDto> customerListToDtoList(List<CustomerEntity> customerEntities);
    List<CustomerEntity> customerListToEntityList(List<CustomerDto> customerDtos);

    @Mapping(source = "name", target = "customerName")
    @Mapping(source = "orders", target = "lastOrderDate", qualifiedByName = "mapLastOrderDate")
    CustomerTableInfoDto customerTableToDtoTable(CustomerEntity customerEntity);

    @Named("mapLastOrderDate")
    default LocalDateTime mapLastOrderDate(List<OrderEntity> orders) {
        return orders.stream()
                .map(OrderEntity::getDropOffDate)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    List<CustomerTableInfoDto> customerTableListToDtoTableList(List<CustomerEntity> customerEntities);

    default Page<CustomerTableInfoDto> customerPageToDtoPage(Page<CustomerEntity> customerEntities, Pageable pageable) {
        List<CustomerTableInfoDto> dtos = customerTableListToDtoTableList(customerEntities.getContent());
        return new PageImpl<>(dtos, pageable, customerEntities.getTotalElements());
    }
}


