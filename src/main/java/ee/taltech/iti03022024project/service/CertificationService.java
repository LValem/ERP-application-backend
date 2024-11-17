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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CertificationService {

    private final CertificationRepository certificationRepository;
    private final CertificationTypeRepository certificationTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final CertificationMapping certificationMapping;

    private static final Logger log = LoggerFactory.getLogger(CertificationService.class);

    public CertificationDto createCertification(CertificationDto certificationDto) {
        log.info("Creating certification for employee with ID: {}", certificationDto.getEmployeeId());

        CertificationTypeEntity certificationType = certificationTypeRepository.findById(certificationDto.getCertificationTypeId())
                .orElseThrow(() -> new NotFoundException("Certification type with ID " + certificationDto.getCertificationTypeId() + " does not exist."));

        EmployeeEntity employee = employeeRepository.findById(certificationDto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + certificationDto.getEmployeeId() + " does not exist."));

        if (certificationRepository.existsByEmployeeAndCertificationType(employee, certificationType)) {
            throw new AlreadyExistsException("Certification for this employee with the same type already exists.");
        }

        CertificationEntity certificationEntity = certificationMapping.certificationToEntity(certificationDto);
        CertificationEntity savedCertification = certificationRepository.save(certificationEntity);

        log.info("Certification created successfully for employee with ID: {}", certificationDto.getEmployeeId());
        return certificationMapping.certificationToDto(savedCertification);
    }

    public List<CertificationDto> getAllCertifications() {
        log.info("Fetching all certifications.");

        List<CertificationEntity> certifications = certificationRepository.findAll();

        log.info("Fetched {} certifications.", certifications.size());
        return certificationMapping.certificationListToDtoList(certifications);
    }

    public Optional<CertificationDto> getCertificationById(Integer id) {
        log.info("Fetching certification with ID: {}", id);

        CertificationEntity certificationEntity = certificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Certification with ID " + id + " does not exist."));

        log.info("Fetched certification with ID: {}", id);
        return Optional.of(certificationMapping.certificationToDto(certificationEntity));
    }

    public Optional<CertificationDto> updateCertification(CertificationDto certificationDto) {
        log.info("Updating certification with ID: {}", certificationDto.getCertificationId());

        CertificationEntity certificationEntity = certificationRepository.findById(certificationDto.getCertificationId())
                .orElseThrow(() -> new NotFoundException("Certification with ID " + certificationDto.getCertificationId() + " does not exist."));

        if (certificationDto.getCertificationTypeId() != null) {
            CertificationTypeEntity certificationType = certificationTypeRepository.findById(certificationDto.getCertificationTypeId())
                    .orElseThrow(() -> new NotFoundException("Certification type with ID " + certificationDto.getCertificationTypeId() + " does not exist."));
            certificationEntity.setCertificationType(certificationType);
        }

        if (certificationDto.getIssuedDate() != null) {
            certificationEntity.setIssuedDate(certificationDto.getIssuedDate());
        }

        if (certificationDto.getExpiryDate() != null) {
            certificationEntity.setExpiryDate(certificationDto.getExpiryDate());
        }

        CertificationEntity updatedCertification = certificationRepository.save(certificationEntity);

        log.info("Certification with ID {} updated successfully.", certificationDto.getCertificationId());
        return Optional.of(certificationMapping.certificationToDto(updatedCertification));
    }
}
