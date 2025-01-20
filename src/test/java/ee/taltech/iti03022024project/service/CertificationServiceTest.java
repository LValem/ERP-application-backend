package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.CertificationDto;
import ee.taltech.iti03022024project.entity.CertificationEntity;
import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import ee.taltech.iti03022024project.entity.EmployeeEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.CertificationMapping;
import ee.taltech.iti03022024project.repository.CertificationRepository;
import ee.taltech.iti03022024project.repository.CertificationTypeRepository;
import ee.taltech.iti03022024project.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificationServiceTest {

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private CertificationTypeRepository certificationTypeRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CertificationMapping certificationMapping;

    @InjectMocks
    private CertificationService certificationService;

    private CertificationDto certificationDto;
    private CertificationEntity certificationEntity;
    private CertificationTypeEntity certificationTypeEntity;
    private EmployeeEntity employeeEntity;

    @BeforeEach
    void setUp() {
        certificationTypeEntity = new CertificationTypeEntity(200, "Health certificate");

        employeeEntity = new EmployeeEntity(100, "Thomas", 1, "heavy");

        certificationEntity = new CertificationEntity(
                1,
                certificationTypeEntity,
                employeeEntity,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(90)
        );

        certificationDto = new CertificationDto();
        certificationDto.setCertificationId(1);
        certificationDto.setCertificationTypeId(200);
        certificationDto.setEmployeeId(100);
        certificationDto.setIssuedDate(LocalDate.now().minusDays(10));
        certificationDto.setExpiryDate(LocalDate.now().plusDays(90));
    }

    // -------------------------------------------------
    // createCertification() Tests
    // -------------------------------------------------
    @Test
    void createCertification_ShouldCreate_WhenValid() {
        when(certificationTypeRepository.findById(certificationDto.getCertificationTypeId()))
                .thenReturn(Optional.of(certificationTypeEntity));
        when(employeeRepository.findById(certificationDto.getEmployeeId()))
                .thenReturn(Optional.of(employeeEntity));
        when(certificationRepository.existsByEmployeeAndCertificationType(employeeEntity, certificationTypeEntity))
                .thenReturn(false);

        when(certificationMapping.certificationToEntity(certificationDto))
                .thenReturn(certificationEntity);

        when(certificationRepository.save(certificationEntity))
                .thenReturn(certificationEntity);

        when(certificationMapping.certificationToDto(certificationEntity))
                .thenReturn(certificationDto);

        CertificationDto result = certificationService.createCertification(certificationDto);

        assertNotNull(result);
        assertEquals(certificationDto.getCertificationId(), result.getCertificationId());
        verify(certificationRepository).save(certificationEntity);
    }

    @Test
    void createCertification_ShouldThrowNotFound_WhenCertificationTypeMissing() {
        when(certificationTypeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> certificationService.createCertification(certificationDto)
        );
        assertTrue(ex.getMessage().contains("Certification type with ID " + certificationDto.getCertificationTypeId()));
        verify(certificationRepository, never()).save(any());
    }

    @Test
    void createCertification_ShouldThrowNotFound_WhenEmployeeMissing() {
        when(certificationTypeRepository.findById(certificationDto.getCertificationTypeId()))
                .thenReturn(Optional.of(certificationTypeEntity));
        when(employeeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> certificationService.createCertification(certificationDto)
        );
        assertTrue(ex.getMessage().contains("Employee with ID " + certificationDto.getEmployeeId()));
        verify(certificationRepository, never()).save(any());
    }

    @Test
    void createCertification_ShouldThrowAlreadyExists_WhenCertForEmployeeExists() {
        when(certificationTypeRepository.findById(certificationDto.getCertificationTypeId()))
                .thenReturn(Optional.of(certificationTypeEntity));
        when(employeeRepository.findById(certificationDto.getEmployeeId()))
                .thenReturn(Optional.of(employeeEntity));
        when(certificationRepository.existsByEmployeeAndCertificationType(employeeEntity, certificationTypeEntity))
                .thenReturn(true);

        AlreadyExistsException ex = assertThrows(
                AlreadyExistsException.class,
                () -> certificationService.createCertification(certificationDto)
        );
        assertTrue(ex.getMessage().contains("already exists"));
        verify(certificationRepository, never()).save(any());
    }

    // -------------------------------------------------
    // getAllCertifications() Tests
    // -------------------------------------------------
    @Test
    void getAllCertifications_ShouldReturnList() {
        CertificationEntity anotherCert = new CertificationEntity(
                2,
                certificationTypeEntity,
                employeeEntity,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        List<CertificationEntity> mockEntities = List.of(certificationEntity, anotherCert);

        when(certificationRepository.findAll()).thenReturn(mockEntities);

        CertificationDto dto2 = new CertificationDto();
        dto2.setCertificationId(2);
        dto2.setCertificationTypeId(200);
        dto2.setEmployeeId(100);
        dto2.setIssuedDate(LocalDate.now());
        dto2.setExpiryDate(LocalDate.now().plusDays(30));

        when(certificationMapping.certificationListToDtoList(mockEntities))
                .thenReturn(List.of(certificationDto, dto2));

        List<CertificationDto> result = certificationService.getAllCertifications();

        assertEquals(2, result.size());
        verify(certificationRepository).findAll();
        verify(certificationMapping).certificationListToDtoList(mockEntities);
    }

    // -------------------------------------------------
    // getCertificationById() Tests
    // -------------------------------------------------
    @Test
    void getCertificationById_ShouldReturn_WhenFound() {
        when(certificationRepository.findById(anyInt()))
                .thenReturn(Optional.of(certificationEntity));
        when(certificationMapping.certificationToDto(certificationEntity))
                .thenReturn(certificationDto);

        Optional<CertificationDto> result = certificationService.getCertificationById(1);

        assertTrue(result.isPresent());
        assertEquals(certificationDto, result.get());
    }

    @Test
    void getCertificationById_ShouldThrowNotFound_WhenNotFound() {
        when(certificationRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> certificationService.getCertificationById(999)
        );
        assertTrue(ex.getMessage().contains("999 does not exist"));
    }

    // -------------------------------------------------
    // updateCertification() Tests
    // -------------------------------------------------
    @Test
    void updateCertification_ShouldUpdate_WhenFound() {
        when(certificationRepository.findById(certificationDto.getCertificationId()))
                .thenReturn(Optional.of(certificationEntity));

        CertificationTypeEntity newType = new CertificationTypeEntity(300, "Dangerous goods");
        certificationDto.setCertificationTypeId(300);

        when(certificationTypeRepository.findById(300))
                .thenReturn(Optional.of(newType));

        when(certificationRepository.save(any(CertificationEntity.class)))
                .thenReturn(certificationEntity);

        when(certificationMapping.certificationToDto(certificationEntity))
                .thenReturn(certificationDto);

        Optional<CertificationDto> updated = certificationService.updateCertification(certificationDto);

        assertTrue(updated.isPresent());
        assertEquals(300, certificationEntity.getCertificationType().getCertificationTypeId());
        assertEquals(certificationDto, updated.get());

        verify(certificationRepository).save(certificationEntity);
    }

    @Test
    void updateCertification_ShouldThrowNotFound_WhenCertDoesNotExist() {
        when(certificationRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> certificationService.updateCertification(certificationDto)
        );
        assertTrue(ex.getMessage().contains("Certification with ID "));
        verify(certificationRepository, never()).save(any());
    }

    @Test
    void updateCertification_ShouldThrowNotFound_WhenCertTypeDoesNotExist() {
        when(certificationRepository.findById(certificationDto.getCertificationId()))
                .thenReturn(Optional.of(certificationEntity));

        certificationDto.setCertificationTypeId(999);
        when(certificationTypeRepository.findById(999))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> certificationService.updateCertification(certificationDto)
        );
        assertTrue(ex.getMessage().contains("Certification type with ID 999"));
        verify(certificationRepository, never()).save(any());
    }
}
