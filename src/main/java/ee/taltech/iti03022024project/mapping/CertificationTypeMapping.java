package ee.taltech.iti03022024project.mapping;

import ee.taltech.iti03022024project.dto.CertificationTypeDto;
import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CertificationTypeMapping {
    CertificationTypeDto certificationTypeToDto(CertificationTypeEntity certificationTypeEntity);
    CertificationTypeEntity certificationTypeToEntity(CertificationTypeDto certificationTypeDto);
    List<CertificationTypeDto> certificationTypeListToDtoList(List<CertificationTypeEntity> certificationTypeEntities);
    List<CertificationTypeEntity> certificationTypeListToEntityList(List<CertificationTypeDto> certificationTypeDtos);
}
