package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.entity.VehicleEntity;
import ee.taltech.iti03022024project.mapping.VehicleMapping;
import ee.taltech.iti03022024project.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapping vehicleMapping;

    public VehicleDto createVehicle(VehicleDto vehicleDto) {
        VehicleEntity vehicleEntity = new VehicleEntity(null, vehicleDto.getVehicleType(),
                null, vehicleDto.getMaxLoad(), vehicleDto.getCurrentFuel(), vehicleDto.getRegistrationPlate());
        VehicleEntity savedVehicleEntity = vehicleRepository.save(vehicleEntity);
        return vehicleMapping.vehicleToDto(savedVehicleEntity);
    }

    public List<VehicleDto> getAllVehicles() {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll();
        return vehicleMapping.vehicleListToDtoList(vehicleEntities);
    }

    public Optional<VehicleDto> getVehicleById(Integer id) {
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(id);
        return vehicleEntity.map(vehicleMapping::vehicleToDto);
    }
}
