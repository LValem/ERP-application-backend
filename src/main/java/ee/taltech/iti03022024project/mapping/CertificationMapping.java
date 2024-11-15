package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.CertificationDto;
import ee.taltech.iti03022024project.entity.CertificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificationMapping {

    @Mapping(source = "employee.employeeId", target = "employeeId")
    @Mapping(source = "certificationType.certificationTypeId", target = "certificationTypeId")
    CertificationDto certificationToDto(CertificationEntity certificationEntity);

    @Mapping(source = "employeeId", target = "employee.employeeId")
    @Mapping(source = "certificationTypeId", target = "certificationType.certificationTypeId")
    CertificationEntity certificationToEntity(CertificationDto certificationDto);

    List<CertificationDto> certificationListToDtoList(List<CertificationEntity> certificationEntities);
    List<CertificationEntity> certificationListToEntityList(List<CertificationDto> certificationDtos);
}
