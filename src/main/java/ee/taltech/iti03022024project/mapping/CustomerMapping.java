package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.CustomerDto;
import ee.taltech.iti03022024project.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapping {
    CustomerDto customerToDto(CustomerEntity customerEntity);
    CustomerEntity customerToEntity(CustomerDto customerDto);
    List<CustomerDto> customerListToDtoList(List<CustomerEntity> customerEntities);
    List<CustomerEntity> customerListToEntityList(List<CustomerDto> customerDtos);
}
