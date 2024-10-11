package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.PermissionDto;
import ee.taltech.iti03022024project.entity.PermissionEntity;
import ee.taltech.iti03022024project.mapping.PermissionMapping;
import ee.taltech.iti03022024project.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapping permissionMapping;

    public PermissionDto createPermission(PermissionDto permissionDto) {
        PermissionEntity permissionEntity = new PermissionEntity(null, permissionDto.getDescription());
        PermissionEntity savedPermissionEntity = permissionRepository.save(permissionEntity);
        return permissionMapping.permissionToDto(savedPermissionEntity);
    }

    public List<PermissionDto> getAllPermissions() {
        List<PermissionEntity> permissionEntities = permissionRepository.findAll();
        return permissionMapping.permissionListToDtoList(permissionEntities);
    }

    public Optional<PermissionDto> getPermissionById(Integer id) {
        Optional<PermissionEntity> permissionEntity = permissionRepository.findById(id);
        return permissionEntity.map(permissionMapping::permissionToDto);
    }
}
