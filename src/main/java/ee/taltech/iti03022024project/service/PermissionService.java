package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.employee.PermissionDto;
import ee.taltech.iti03022024project.entity.PermissionEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.PermissionMapping;
import ee.taltech.iti03022024project.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapping permissionMapping;

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);

    public PermissionDto createPermission(PermissionDto permissionDto) {
        if (permissionRepository.existsByDescriptionIgnoreCase(permissionDto.getDescription())) {
            throw new AlreadyExistsException("Permission with name " + permissionDto.getDescription() + " already exists.");
        }
        PermissionEntity permissionEntity = new PermissionEntity(null, permissionDto.getDescription());
        PermissionEntity savedPermissionEntity = permissionRepository.save(permissionEntity);

        log.info("Created permission with description: {}", permissionDto.getDescription());

        return permissionMapping.permissionToDto(savedPermissionEntity);
    }

    public List<PermissionDto> getAllPermissions() {
        List<PermissionEntity> permissionEntities = permissionRepository.findAll();

        if (permissionEntities.isEmpty()) {
            throw new NotFoundException("There are no permissions!");
        }

        log.info("Fetched all permissions, count: {}", permissionEntities.size());

        return permissionMapping.permissionListToDtoList(permissionEntities);
    }

    public Optional<PermissionDto> getPermissionById(Integer id) {
        Optional<PermissionEntity> permissionEntity = permissionRepository.findById(id);

        if (permissionEntity.isEmpty()) {
            throw new NotFoundException("Permission with this ID does not exist");
        }

        log.info("Fetched permission with ID: {}", id);

        return permissionEntity.map(permissionMapping::permissionToDto);
    }
}
