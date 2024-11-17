package ee.taltech.iti03022024project.service;

import ee.taltech.iti03022024project.dto.CertificationTypeDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.searchcriteria.CertificationTypeSearchCriteria;
import ee.taltech.iti03022024project.entity.CertificationTypeEntity;
import ee.taltech.iti03022024project.exception.AlreadyExistsException;
import ee.taltech.iti03022024project.exception.NotFoundException;
import ee.taltech.iti03022024project.mapping.CertificationTypeMapping;
import ee.taltech.iti03022024project.repository.CertificationTypeRepository;
import ee.taltech.iti03022024project.repository.specifications.CertificationTypeSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CertificationTypeService {

    private final CertificationTypeRepository certificationTypeRepository;
    private final CertificationTypeMapping certificationTypeMapping;

    public CertificationTypeDto createCertificationType(CertificationTypeDto CertificationTypeDto) {
        if (certificationTypeRepository.existsByCertificationNameIgnoreCase(CertificationTypeDto.getCertificationName())) {
            throw new AlreadyExistsException("CertificationType with name " + CertificationTypeDto.getCertificationName() + " already exists.");
        }
        CertificationTypeEntity certificationTypeEntity = new CertificationTypeEntity(null, CertificationTypeDto.getCertificationName());
        CertificationTypeEntity savedCertificationTypeEntity = certificationTypeRepository.save(certificationTypeEntity);
        return certificationTypeMapping.certificationTypeToDto(savedCertificationTypeEntity);
    }
    
    public List<CertificationTypeDto> getAllCertificationTypes() {
        return certificationTypeMapping.certificationTypeListToDtoList(certificationTypeRepository.findAll());
    }

    public Optional<CertificationTypeDto> getCertificationTypeById(Integer id) {
        Optional<CertificationTypeEntity> certificationTypeEntity = certificationTypeRepository.findById(id);
        certificationTypeEntity.orElseThrow(() -> new NotFoundException("Certification type with this ID does not exist"));
        return certificationTypeEntity.map(certificationTypeMapping::certificationTypeToDto);
    }

    public Optional<CertificationTypeDto> updateCertificationType(CertificationTypeDto certificationTypeDto) {
        Optional<CertificationTypeEntity> certificationTypeEntityOpt = certificationTypeRepository.findById(certificationTypeDto.getCertificationTypeId());

        CertificationTypeEntity certificationTypeEntity = certificationTypeEntityOpt
                .orElseThrow(() -> new NotFoundException("Certification type with this ID does not exist"));

        if (!certificationTypeDto.getCertificationName().isEmpty() && !certificationTypeRepository.existsByCertificationNameIgnoreCase(certificationTypeDto.getCertificationName())) {
            certificationTypeEntity.setCertificationName(certificationTypeDto.getCertificationName());
        } else {
            throw new AlreadyExistsException("Cannot change name to " + certificationTypeDto.getCertificationName() + ", because it already exists!");
        }

        CertificationTypeEntity updatedCertificationType = certificationTypeRepository.save(certificationTypeEntity);
        return Optional.of(certificationTypeMapping.certificationTypeToDto(updatedCertificationType));
    }

    public PageResponse<CertificationTypeDto> searchCertificationTypes(CertificationTypeSearchCriteria criteria) {
        // Default sorting and pagination
        String sortBy = criteria.getSortBy() != null ? criteria.getSortBy() : "certificationTypeId";
        String direction = criteria.getSortDirection() != null ? criteria.getSortDirection().toUpperCase() : "ASC";
        int pageNumber = criteria.getPage() != null ? criteria.getPage() : 0;
        int pageSize = criteria.getSize() != null ? criteria.getSize() : 20;

        // Create Sort and Pageable
        Sort sort = Sort.by(Sort.Direction.valueOf(direction), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Build the specification
        Specification<CertificationTypeEntity> spec = Specification
                .where(CertificationTypeSpecifications.certificationTypeId(criteria.getCertificationTypeId()))
                .and(CertificationTypeSpecifications.certificationNameLike(criteria.getCertificationName()));

        // Execute query with pagination
        Page<CertificationTypeEntity> certificationTypePage = certificationTypeRepository.findAll(spec, pageable);

        // Map Page<CertificationTypeEntity> to Page<CertificationTypeDto>
        Page<CertificationTypeDto> dtoPage = certificationTypePage.map(certificationType -> new CertificationTypeDto(
                certificationType.getCertificationTypeId(),
                certificationType.getCertificationName()
        ));

        // Return a PageResponse wrapping the Page<CertificationTypeDto>
        return new PageResponse<>(dtoPage);
    }
}
