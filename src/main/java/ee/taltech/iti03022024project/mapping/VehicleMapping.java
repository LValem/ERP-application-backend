package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.entity.VehicleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapping {
    VehicleDto vehicleToDto(VehicleEntity vehicleEntity);
    VehicleEntity vehicleToEntity(VehicleDto vehicleDto);
    List<VehicleDto> vehicleListToDtoList(List<VehicleEntity> vehicleEntities);
    List<VehicleEntity> vehicleListToEntityList(List<VehicleDto> vehicleDtos);
}
