package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.employee.EmployeeDto;
import ee.taltech.iti03022024project.dto.query.EmployeeTableInfoDto;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.entity.JobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapping {
    EmployeeDto employeeToDto(EmployeeEntity employeeEntity);
    EmployeeEntity employeeToEntity(EmployeeDto employeeDto);
    List<EmployeeDto> employeeListToDtoList(List<EmployeeEntity> employeeEntities);
    List<EmployeeEntity> employeeListToEntityList(List<EmployeeDto> employeeDtos);

    default String mapPermissionIdToDescription(Integer permissionId) {
        if (permissionId == null) return "UNKNOWN";
        return switch (permissionId) {
            case 1 -> "ADMIN";
            case 2 -> "USER";
            case 3 -> "DRIVER";
            default -> "UNKNOWN";
        };
    }

    default EmployeeTableInfoDto employeeToTableInfoDto(EmployeeEntity employeeEntity) {
        String permissionDescription = mapPermissionIdToDescription(employeeEntity.getPermissionId());

        String certifications = employeeEntity.getCertifications() != null
                ? employeeEntity.getCertifications().stream()
                .filter(cert -> cert.getCertificationType() != null)
                .map(cert -> cert.getCertificationType().getCertificationName())
                .collect(Collectors.joining(", "))
                : "";

        LocalDateTime lastJobDate = employeeEntity.getJobs() != null
                ? employeeEntity.getJobs().stream()
                .map(JobEntity::getDropOffDate)
                .filter(date -> date != null)
                .max(LocalDateTime::compareTo)
                .orElse(null)
                : null;

        return new EmployeeTableInfoDto(
                employeeEntity.getEmployeeId(),
                employeeEntity.getName(),
                permissionDescription,
                certifications,
                lastJobDate
        );
    }

    default List<EmployeeTableInfoDto> employeeListToTableInfoDtoList(List<EmployeeEntity> employeeEntities) {
        return employeeEntities != null
                ? employeeEntities.stream()
                .map(this::employeeToTableInfoDto)
                .collect(Collectors.toList())
                : List.of();
    }

    default Page<EmployeeTableInfoDto> employeePageToTableInfoDtoPage(Page<EmployeeEntity> employeePage, Pageable pageable) {
        List<EmployeeTableInfoDto> dtoList = employeePage != null
                ? employeePage.stream()
                .map(this::employeeToTableInfoDto)
                .collect(Collectors.toList())
                : List.of();
        return new PageImpl<>(dtoList, pageable, employeePage != null ? employeePage.getTotalElements() : 0);
    }
}
