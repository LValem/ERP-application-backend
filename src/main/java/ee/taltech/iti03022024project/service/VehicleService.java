package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.entity.VehicleEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.exception.WrongValueException;
import ee.taltech.iti03022024project.mapping.VehicleMapping;
import ee.taltech.iti03022024project.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapping vehicleMapping;

    private static final Logger log = LoggerFactory.getLogger(VehicleService.class);

    public VehicleDto createVehicle(VehicleDto vehicleDto) {
        if (vehicleRepository.existsByRegistrationPlate(vehicleDto.getRegistrationPlate())) {
            throw new AlreadyExistsException
                    ("vehicle with registration plate " + vehicleDto.getRegistrationPlate() + " already exists.");
        }
        VehicleEntity vehicleEntity = new VehicleEntity(null, vehicleDto.getVehicleType(),
                null, vehicleDto.getMaxLoad(), vehicleDto.getCurrentFuel(), vehicleDto.getRegistrationPlate());
        VehicleEntity savedVehicleEntity = vehicleRepository.save(vehicleEntity);

        log.info("Created vehicle with registration plate: {}", vehicleDto.getRegistrationPlate());

        return vehicleMapping.vehicleToDto(savedVehicleEntity);
    }

    public List<VehicleDto> getAllVehicles() {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll();
        if (vehicleEntities.isEmpty()) {
            throw new NotFoundException("There are no vehicles!");
        }

        log.info("Fetched all vehicles, count: {}", vehicleEntities.size());

        return vehicleMapping.vehicleListToDtoList(vehicleEntities);
    }

    public Optional<VehicleDto> getVehicleById(Integer id) {
        log.info("Fetching vehicle with id {}", id);
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(id);
        if (vehicleEntity.isEmpty()) {
            throw new NotFoundException("There is no vehicle with id " + id);
        }

        log.info("Successfully fetched vehicle with ID: {}", id);

        return vehicleEntity.map(vehicleMapping::vehicleToDto);
    }

    public Optional<VehicleDto> updateVehicle(Integer id, Character vehicleType, Boolean isInUse,
                                              Integer maxLoad, Integer currentFuel, String registrationPlate) {
        // Find the vehicle by ID
        Optional<VehicleEntity> vehicleEntityOpt = vehicleRepository.findById(id);

        // Throw an exception if the vehicle is not found
        VehicleEntity vehicleEntity = vehicleEntityOpt
                .orElseThrow(() -> new NotFoundException("Vehicle with this ID does not exist"));

        // Update fields if the new values are provided (not null)
        if (vehicleType != null) {
            vehicleEntity.setVehicleType(vehicleType);
        }

        if (isInUse != null) {
            vehicleEntity.setIsInUse(isInUse);
        }

        if (maxLoad != null) {
            vehicleEntity.setMaxLoad(maxLoad);
        }

        if (currentFuel != null) {
            vehicleEntity.setCurrentFuel(currentFuel);
        }

        if (registrationPlate != null && !registrationPlate.equals(getVehicleById(id).get().getRegistrationPlate())) {
            if (!vehicleRepository.existsByRegistrationPlate(registrationPlate)) {
                if (registrationPlate.length() == 6) {
                    vehicleEntity.setRegistrationPlate(registrationPlate);
                } else {
                    throw new WrongValueException("Numberplate doesn't match criteria!");
                }
            } else {
                throw new AlreadyExistsException("Vehicle with this Registration Plate already exists!");
            }
        }

        // Save the updated vehicle entity
        VehicleEntity updatedVehicle = vehicleRepository.save(vehicleEntity);

        log.info("Updated vehicle with ID: {}", id);

        // Convert the entity to a DTO and return it
        return Optional.of(vehicleMapping.vehicleToDto(updatedVehicle));
    }
}