package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.JobDto;
import ee.taltech.iti03022024project.dto.query.DoneJobTableInfoDto;
import ee.taltech.iti03022024project.dto.query.NotDoneJobTableInfoDto;
import ee.taltech.iti03022024project.entity.JobEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    @Mapping(source = "vehicle.vehicleId", target = "vehicleId")
    @Mapping(source = "vehicle.registrationPlate", target = "registrationPlate")
    @Mapping(source = "fuelConsumption.fuelUsed", target = "fuelUsed")
    @Mapping(source = "fuelConsumption.distanceDriven", target = "distanceDriven")
    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "order.customer.name", target = "customerName")
    DoneJobTableInfoDto jobToDoneJobDto(JobEntity jobEntity);

    List<DoneJobTableInfoDto> jobListToDoneJobDtoList(List<JobEntity> jobEntities);

    default Page<DoneJobTableInfoDto> jobPageToDoneJobDtoPage(Page<JobEntity> jobEntities, Pageable pageable) {
        List<DoneJobTableInfoDto> dtos = jobListToDoneJobDtoList(jobEntities.getContent());
        return new PageImpl<>(dtos, pageable, jobEntities.getTotalElements());
    }

    @Mapping(source = "vehicle.vehicleId", target = "vehicleId")
    @Mapping(source = "vehicle.registrationPlate", target = "registrationPlate")
    @Mapping(source = "order.orderId", target = "orderId")
    @Mapping(source = "order.customer.name", target = "customerName")
    @Mapping(source = "order.pickupDate", target = "pickupDate")
    @Mapping(source = "order.dropOffDate", target = "dropOffDate")
    NotDoneJobTableInfoDto jobToNotDoneJobDto(JobEntity jobEntity);

    List<NotDoneJobTableInfoDto> jobListToNotDoneJobDtoList(List<JobEntity> jobEntities);

    default Page<NotDoneJobTableInfoDto> jobPageToNotDoneJobDtoPage(Page<JobEntity> jobEntities, Pageable pageable) {
        List<NotDoneJobTableInfoDto> dtos = jobListToNotDoneJobDtoList(jobEntities.getContent());
        return new PageImpl<>(dtos, pageable, jobEntities.getTotalElements());
    }
}
