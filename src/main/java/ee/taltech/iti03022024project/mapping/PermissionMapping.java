package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.PermissionDto;
import ee.taltech.iti03022024project.entity.PermissionEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapping {
    PermissionDto permissionToDto(PermissionEntity permissionEntity);
    PermissionEntity permissionToEntity(PermissionDto permissionDto);
    List<PermissionDto> permissionListToDtoList(List<PermissionEntity> permissionEntities);
    List<PermissionEntity> permissionListToEntityList(List<PermissionDto> permissionDtos);
}
