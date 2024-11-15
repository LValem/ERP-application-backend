package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.JobDto;
import ee.taltech.iti03022024project.entity.JobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobMapping {

    @Mapping(source = "vehicle.vehicleId", target = "vehicleId")
    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "order.orderId", target = "orderId")
    JobDto jobToDto(JobEntity jobEntity);

    @Mapping(source = "vehicleId", target = "vehicle.vehicleId")
    @Mapping(source = "employeeId", target = "employee.employeeId")
    @Mapping(source = "orderId", target = "order.orderId")
    JobEntity jobToEntity(JobDto jobDto);

    List<JobDto> jobListToDtoList(List<JobEntity> jobEntities);
    List<JobEntity> jobListToEntityList(List<JobDto> jobDtos);
}
