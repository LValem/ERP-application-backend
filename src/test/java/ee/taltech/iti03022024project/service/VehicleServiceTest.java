package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.entity.VehicleEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.exception.WrongValueException;
import ee.taltech.iti03022024project.mapping.VehicleMapping;
import ee.taltech.iti03022024project.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapping vehicleMapping;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleDto vehicleDto;
    private VehicleEntity vehicleEntity;

    @BeforeEach
    void setUp() {
        vehicleDto = new VehicleDto();
        vehicleDto.setVehicleId(1);
        vehicleDto.setVehicleType('T');
        vehicleDto.setRegistrationPlate("ABC123");
        vehicleDto.setMaxLoad(2000);
        vehicleDto.setCurrentFuel(80);

        vehicleEntity = new VehicleEntity();
        vehicleEntity.setVehicleId(1);
        vehicleEntity.setVehicleType('T');
        vehicleEntity.setRegistrationPlate("ABC123");
        vehicleEntity.setMaxLoad(2000);
        vehicleEntity.setCurrentFuel(80);
    }

    @Test
    void createVehicle_ShouldCreateWhenPlateIsUnique() {
        when(vehicleRepository.existsByRegistrationPlate("ABC123")).thenReturn(false);
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(vehicleEntity);
        when(vehicleMapping.vehicleToDto(vehicleEntity)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(vehicleDto);

        assertNotNull(result);
        assertEquals("ABC123", result.getRegistrationPlate());
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }

    @Test
    void createVehicle_ShouldThrowAlreadyExistsWhenPlateDuplicate() {
        when(vehicleRepository.existsByRegistrationPlate("ABC123")).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> vehicleService.createVehicle(vehicleDto));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void getAllVehicles_ShouldReturnList() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicleEntity));
        when(vehicleMapping.vehicleListToDtoList(List.of(vehicleEntity))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.getAllVehicles();

        assertEquals(1, result.size());
        assertEquals("ABC123", result.getFirst().getRegistrationPlate());
        verify(vehicleRepository).findAll();
    }

    @Test
    void getAllVehicles_ShouldThrowNotFoundWhenEmpty() {
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(NotFoundException.class, () -> vehicleService.getAllVehicles());
    }

    @Test
    void getVehicleById_ShouldReturnWhenFound() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleMapping.vehicleToDto(vehicleEntity)).thenReturn(vehicleDto);

        Optional<VehicleDto> result = vehicleService.getVehicleById(1);

        assertTrue(result.isPresent());
        assertEquals("ABC123", result.get().getRegistrationPlate());
    }

    @Test
    void getVehicleById_ShouldThrowNotFoundWhenMissing() {
        when(vehicleRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> vehicleService.getVehicleById(999));
    }

    @Test
    void updateVehicle_ShouldUpdateWhenValid() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicleEntity));

        VehicleDto oldVehicleDto = new VehicleDto();
        oldVehicleDto.setVehicleId(1);
        oldVehicleDto.setRegistrationPlate("ABC123");

        VehicleEntity updatedEntity = new VehicleEntity();
        updatedEntity.setVehicleId(1);
        updatedEntity.setVehicleType('C');
        updatedEntity.setIsInUse(true);
        updatedEntity.setMaxLoad(3000);
        updatedEntity.setCurrentFuel(90);
        updatedEntity.setRegistrationPlate("XYZ999");

        VehicleDto updatedVehicleDto = new VehicleDto();
        updatedVehicleDto.setVehicleId(1);
        updatedVehicleDto.setRegistrationPlate("XYZ999");

        when(vehicleMapping.vehicleToDto(vehicleEntity)).thenReturn(oldVehicleDto);
        when(vehicleMapping.vehicleToDto(updatedEntity)).thenReturn(updatedVehicleDto);

        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleRepository.save(any(VehicleEntity.class))).thenReturn(updatedEntity);

        Optional<VehicleDto> result = vehicleService.updateVehicle(
                1, 'C', true, 3000, 90, "XYZ999"
        );

        assertTrue(result.isPresent());
        assertEquals("XYZ999", result.get().getRegistrationPlate());
        verify(vehicleRepository).save(any(VehicleEntity.class));
    }


    @Test
    void updateVehicle_ShouldThrowNotFoundWhenMissing() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> vehicleService.updateVehicle(
                1, 'C', true, 3000, 90, "XYZ999"
        ));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateVehicle_ShouldThrowAlreadyExistsWhenPlateExists() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleMapping.vehicleToDto(vehicleEntity)).thenReturn(vehicleDto);
        when(vehicleRepository.existsByRegistrationPlate("XYZ999")).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> vehicleService.updateVehicle(
                1, 'C', true, 3000, 90, "XYZ999"
        ));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void updateVehicle_ShouldThrowWrongValueWhenPlateNot6Length() {
        when(vehicleRepository.findById(1)).thenReturn(Optional.of(vehicleEntity));
        when(vehicleMapping.vehicleToDto(vehicleEntity)).thenReturn(vehicleDto);
        when(vehicleRepository.existsByRegistrationPlate("XXXXXX7")).thenReturn(false);

        assertThrows(WrongValueException.class, () -> vehicleService.updateVehicle(
                1, 'C', true, 3000, 90, "XXXXXX7"
        ));
        verify(vehicleRepository, never()).save(any());
    }
}
