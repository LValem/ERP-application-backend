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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CertificationTypeService.class);

    public CertificationTypeDto createCertificationType(CertificationTypeDto certificationTypeDto) {
        log.info("Attempting to create certification type with name: {}", certificationTypeDto.getCertificationName());

        if (certificationTypeRepository.existsByCertificationNameIgnoreCase(certificationTypeDto.getCertificationName())) {
            throw new AlreadyExistsException("CertificationType with name " + certificationTypeDto.getCertificationName() + " already exists.");
        }

        CertificationTypeEntity certificationTypeEntity = new CertificationTypeEntity(null, certificationTypeDto.getCertificationName());
        CertificationTypeEntity savedCertificationTypeEntity = certificationTypeRepository.save(certificationTypeEntity);

        log.info("Certification type created successfully with name: {}", certificationTypeDto.getCertificationName());
        return certificationTypeMapping.certificationTypeToDto(savedCertificationTypeEntity);
    }

    public List<CertificationTypeDto> getAllCertificationTypes() {
        log.info("Fetching all certification types.");

        List<CertificationTypeDto> certificationTypeDtos = certificationTypeMapping.certificationTypeListToDtoList(certificationTypeRepository.findAll());

        log.info("Fetched {} certification types.", certificationTypeDtos.size());
        return certificationTypeDtos;
    }

    public Optional<CertificationTypeDto> getCertificationTypeById(Integer id) {
        log.info("Fetching certification type with ID: {}", id);

        Optional<CertificationTypeEntity> certificationTypeEntity = certificationTypeRepository.findById(id);
        if (certificationTypeEntity.isEmpty()) {
            throw new NotFoundException("CertificationType with ID " + id + " not found.");
        }

        log.info("Fetched certification type with ID: {}", id);
        return certificationTypeEntity.map(certificationTypeMapping::certificationTypeToDto);
    }

    public Optional<CertificationTypeDto> updateCertificationType(CertificationTypeDto certificationTypeDto) {
        log.info("Attempting to update certification type with ID: {}", certificationTypeDto.getCertificationTypeId());

        Optional<CertificationTypeEntity> certificationTypeEntityOpt = certificationTypeRepository.findById(certificationTypeDto.getCertificationTypeId());

        CertificationTypeEntity certificationTypeEntity = certificationTypeEntityOpt
                .orElseThrow(() -> new NotFoundException("Certification type with this ID does not exist"));

        if (certificationTypeDto.getCertificationName() != null && !certificationTypeDto.getCertificationName().equals(getCertificationTypeById(certificationTypeDto.getCertificationTypeId()).get().getCertificationName())) {
            if (!certificationTypeDto.getCertificationName().isEmpty() && !certificationTypeRepository.existsByCertificationNameIgnoreCase(certificationTypeDto.getCertificationName())) {
                certificationTypeEntity.setCertificationName(certificationTypeDto.getCertificationName());
            } else {
                throw new AlreadyExistsException("Cannot change name to " + certificationTypeDto.getCertificationName() + ", because it already exists!");
            }
        }


        CertificationTypeEntity updatedCertificationType = certificationTypeRepository.save(certificationTypeEntity);

        log.info("Certification type with ID {} updated successfully.", certificationTypeDto.getCertificationTypeId());
        return Optional.of(certificationTypeMapping.certificationTypeToDto(updatedCertificationType));
    }

    public PageResponse<CertificationTypeDto> searchCertificationTypes(CertificationTypeSearchCriteria criteria) {
        log.info("Searching certification types with criteria: {}", criteria);

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

        log.info("Search completed. Found {} certification types.", dtoPage.getTotalElements());
        // Return a PageResponse wrapping the Page<CertificationTypeDto>
        return new PageResponse<>(dtoPage);
    }
}
