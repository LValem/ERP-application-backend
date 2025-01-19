package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.dto.query.VehicleTableInfoDto;
import ee.taltech.iti03022024project.entity.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapping {
    VehicleDto vehicleToDto(VehicleEntity vehicleEntity);
    VehicleEntity vehicleToEntity(VehicleDto vehicleDto);
    List<VehicleDto> vehicleListToDtoList(List<VehicleEntity> vehicleEntities);
    List<VehicleEntity> vehicleListToEntityList(List<VehicleDto> vehicleDtos);

    @Mapping(source = "vehicleId", target = "vehicleId")
    @Mapping(source = "vehicleType", target = "vehicleType")
    @Mapping(source = "isInUse", target = "isInUse")
    @Mapping(source = "maxLoad", target = "maxLoad")
    @Mapping(source = "currentFuel", target = "currentFuel")
    @Mapping(source = "registrationPlate", target = "registrationPlate")
    VehicleTableInfoDto vehicleToTableInfoDto(VehicleEntity vehicleEntity);

    List<VehicleTableInfoDto> vehicleListToTableInfoDtoList(List<VehicleEntity> vehicleEntities);

    default Page<VehicleTableInfoDto> vehiclePageToTableInfoDtoPage(Page<VehicleEntity> vehicleEntities, Pageable pageable) {
        List<VehicleTableInfoDto> dtos = vehicleListToTableInfoDtoList(vehicleEntities.getContent());
        return new PageImpl<>(dtos, pageable, vehicleEntities.getTotalElements());
    }
}
