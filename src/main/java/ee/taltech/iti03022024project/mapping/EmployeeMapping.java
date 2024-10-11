package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.EmployeeDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapping {
    EmployeeDto employeeToDto(EmployeeEntity employeeEntity);
    EmployeeEntity employeeToEntity(EmployeeDto employeeDto);
    List<EmployeeDto> employeeListToDtoList(List<EmployeeEntity> employeeEntities);
    List<EmployeeEntity> employeeListToEntityList(List<EmployeeDto> employeeDtos);
}
