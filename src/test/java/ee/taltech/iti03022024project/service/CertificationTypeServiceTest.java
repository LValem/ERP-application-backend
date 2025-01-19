package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.CertificationTypeDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.searchcriteria.CertificationTypeSearchCriteria;
import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.CertificationTypeMapping;
import ee.taltech.iti03022024project.repository.CertificationTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificationTypeServiceTest {

    @Mock
    private CertificationTypeRepository certificationTypeRepository;

    @Mock
    private CertificationTypeMapping certificationTypeMapping;

    @InjectMocks
    private CertificationTypeService certificationTypeService;

    private CertificationTypeDto certificationTypeDto;
    private CertificationTypeEntity certificationTypeEntity;

    @BeforeEach
    void setUp() {
        certificationTypeDto = new CertificationTypeDto();
        certificationTypeDto.setCertificationTypeId(1);
        certificationTypeDto.setCertificationName("Chemicals");

        certificationTypeEntity = new CertificationTypeEntity(1, "Chemicals");
    }

    @Test
    void createCertificationType_ShouldCreateWhenNameIsUnique() {
        when(certificationTypeRepository.existsByCertificationNameIgnoreCase("Chemicals"))
                .thenReturn(false);
        when(certificationTypeRepository.save(any(CertificationTypeEntity.class)))
                .thenReturn(certificationTypeEntity);
        when(certificationTypeMapping.certificationTypeToDto(certificationTypeEntity))
                .thenReturn(certificationTypeDto);

        CertificationTypeDto result = certificationTypeService.createCertificationType(certificationTypeDto);

        assertNotNull(result);
        assertEquals("Chemicals", result.getCertificationName());
        verify(certificationTypeRepository).save(any(CertificationTypeEntity.class));
    }

    @Test
    void createCertificationType_ShouldThrowAlreadyExistsWhenNameIsDuplicate() {
        when(certificationTypeRepository.existsByCertificationNameIgnoreCase("Chemicals"))
                .thenReturn(true);

        assertThrows(
                AlreadyExistsException.class,
                () -> certificationTypeService.createCertificationType(certificationTypeDto)
        );
        verify(certificationTypeRepository, never()).save(any(CertificationTypeEntity.class));
    }

    @Test
    void getAllCertificationTypes_ShouldReturnList() {
        List<CertificationTypeEntity> entities = List.of(certificationTypeEntity);
        when(certificationTypeRepository.findAll()).thenReturn(entities);

        when(certificationTypeMapping.certificationTypeListToDtoList(entities))
                .thenReturn(List.of(certificationTypeDto));

        List<CertificationTypeDto> result = certificationTypeService.getAllCertificationTypes();

        assertEquals(1, result.size());
        assertEquals("Chemicals", result.get(0).getCertificationName());
        verify(certificationTypeRepository).findAll();
    }

    @Test
    void getCertificationTypeById_ShouldReturnWhenEntityFound() {
        when(certificationTypeRepository.findById(1))
                .thenReturn(Optional.of(certificationTypeEntity));
        when(certificationTypeMapping.certificationTypeToDto(certificationTypeEntity))
                .thenReturn(certificationTypeDto);

        Optional<CertificationTypeDto> result = certificationTypeService.getCertificationTypeById(1);

        assertTrue(result.isPresent());
        assertEquals("Chemicals", result.get().getCertificationName());
    }

    @Test
    void getCertificationTypeById_ShouldThrowNotFoundWhenEntityDoesNotExist() {
        when(certificationTypeRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> certificationTypeService.getCertificationTypeById(999)
        );
    }

    @Test
    void updateCertificationType_ShouldUpdateNameWhenNewNameIsUnique() {
        when(certificationTypeRepository.findById(1))
                .thenReturn(Optional.of(certificationTypeEntity));
        when(certificationTypeRepository.save(certificationTypeEntity))
                .thenReturn(certificationTypeEntity);

        when(certificationTypeMapping.certificationTypeToDto(certificationTypeEntity))
                .thenReturn(certificationTypeDto);

        CertificationTypeDto incomingDto = new CertificationTypeDto();
        incomingDto.setCertificationTypeId(1);
        incomingDto.setCertificationName("New Name");

        CertificationTypeDto updatedDto = new CertificationTypeDto(1, "New Name");
        when(certificationTypeMapping.certificationTypeToDto(certificationTypeEntity))
                .thenReturn(updatedDto);

        Optional<CertificationTypeDto> result = certificationTypeService.updateCertificationType(incomingDto);

        assertTrue(result.isPresent());
        assertEquals("New Name", result.get().getCertificationName());
        verify(certificationTypeRepository).save(certificationTypeEntity);
    }

    @Test
    void updateCertificationType_ShouldThrowAlreadyExistsWhenNewNameIsDuplicate() {
        when(certificationTypeRepository.findById(1))
                .thenReturn(Optional.of(certificationTypeEntity));

        CertificationTypeDto existingDto = new CertificationTypeDto(1, "Chemicals");
        when(certificationTypeMapping.certificationTypeToDto(certificationTypeEntity))
                .thenReturn(existingDto);

        CertificationTypeDto incomingDto = new CertificationTypeDto();
        incomingDto.setCertificationTypeId(1);
        incomingDto.setCertificationName("Duplicate");

        when(certificationTypeRepository.existsByCertificationNameIgnoreCase("Duplicate"))
                .thenReturn(true);

        assertThrows(
                AlreadyExistsException.class,
                () -> certificationTypeService.updateCertificationType(incomingDto)
        );
        verify(certificationTypeRepository, never()).save(any(CertificationTypeEntity.class));
    }


    @Test
    void updateCertificationType_ShouldThrowNotFoundWhenEntityNotFound() {
        when(certificationTypeRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> certificationTypeService.updateCertificationType(certificationTypeDto)
        );
        verify(certificationTypeRepository, never()).save(any(CertificationTypeEntity.class));
    }

    @Test
    void searchCertificationTypes_ShouldReturnPagedResults() {
        CertificationTypeSearchCriteria criteria = new CertificationTypeSearchCriteria();
        criteria.setCertificationName("Chemicals");
        criteria.setPage(0);
        criteria.setSize(10);

        Page<CertificationTypeEntity> page = new PageImpl<>(List.of(certificationTypeEntity));
        when(certificationTypeRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<CertificationTypeDto> response = certificationTypeService.searchCertificationTypes(criteria);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(certificationTypeRepository).findAll(any(Specification.class), any(Pageable.class));
    }
}
