package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.employee.PermissionDto;
import ee.taltech.iti03022024project.entity.PermissionEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.PermissionMapping;
import ee.taltech.iti03022024project.repository.PermissionRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private PermissionMapping permissionMapping;

    @InjectMocks
    private PermissionService permissionService;

    private PermissionDto permissionDto;
    private PermissionEntity permissionEntity;

    @BeforeEach
    void setUp() {
        permissionDto = new PermissionDto("ADMIN");
        permissionEntity = new PermissionEntity(1, "ADMIN");
    }

    @Test
    void createPermission_ShouldCreateWhenUnique() {
        when(permissionRepository.existsByDescriptionIgnoreCase("ADMIN")).thenReturn(false);
        when(permissionRepository.save(any(PermissionEntity.class))).thenReturn(permissionEntity);
        when(permissionMapping.permissionToDto(permissionEntity)).thenReturn(permissionDto);

        PermissionDto result = permissionService.createPermission(permissionDto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getDescription());
        verify(permissionRepository).save(any(PermissionEntity.class));
    }

    @Test
    void createPermission_ShouldThrowAlreadyExistsWhenDuplicate() {
        when(permissionRepository.existsByDescriptionIgnoreCase("ADMIN")).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> permissionService.createPermission(permissionDto));
        verify(permissionRepository, never()).save(any());
    }

    @Test
    void getAllPermissions_ShouldReturnList() {
        List<PermissionEntity> entities = List.of(permissionEntity);
        when(permissionRepository.findAll()).thenReturn(entities);

        List<PermissionDto> dtos = List.of(permissionDto);
        when(permissionMapping.permissionListToDtoList(entities)).thenReturn(dtos);

        List<PermissionDto> result = permissionService.getAllPermissions();

        assertEquals(1, result.size());
        assertEquals("ADMIN", result.get(0).getDescription());
        verify(permissionRepository).findAll();
    }

    @Test
    void getAllPermissions_ShouldThrowNotFoundWhenEmpty() {
        when(permissionRepository.findAll()).thenReturn(Collections.emptyList());
        assertThrows(NotFoundException.class, () -> permissionService.getAllPermissions());
    }

    @Test
    void getPermissionById_ShouldReturnWhenFound() {
        when(permissionRepository.findById(1)).thenReturn(Optional.of(permissionEntity));
        when(permissionMapping.permissionToDto(permissionEntity)).thenReturn(permissionDto);

        Optional<PermissionDto> result = permissionService.getPermissionById(1);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getDescription());
    }

    @Test
    void getPermissionById_ShouldThrowNotFoundWhenMissing() {
        when(permissionRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> permissionService.getPermissionById(999));
    }
}
