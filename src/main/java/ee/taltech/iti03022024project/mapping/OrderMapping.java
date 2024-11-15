package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.OrderDto;
import ee.taltech.iti03022024project.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapping {

    @Mapping(source = "customer.customerId", target = "customerId")
    OrderDto orderToDto(OrderEntity orderEntity);

    @Mapping(source = "customerId", target = "customer.customerId")
    OrderEntity orderToEntity(OrderDto orderDto);

    List<OrderDto> orderListToDtoList(List<OrderEntity> orderEntities);
    List<OrderEntity> orderListToEntityList(List<OrderDto> orderDtos);
}
