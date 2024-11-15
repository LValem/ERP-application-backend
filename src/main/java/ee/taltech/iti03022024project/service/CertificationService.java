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


    public CertificationDto createCertification(CertificationDto certificationDto) {
        CertificationTypeEntity certificationType = certificationTypeRepository.findById(certificationDto.getCertificationTypeId())
                .orElseThrow(() -> new NotFoundException("Certification type with ID " + certificationDto.getCertificationTypeId() + " does not exist."));

        EmployeeEntity employee = employeeRepository.findById(certificationDto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee with ID " + certificationDto.getEmployeeId() + " does not exist."));

        if (certificationRepository.existsByEmployeeAndCertificationType(employee, certificationType)) {
            throw new AlreadyExistsException("Certification for this employee with the same type already exists.");
        }

        CertificationEntity certificationEntity = certificationMapping.certificationToEntity(certificationDto);
        CertificationEntity savedCertification = certificationRepository.save(certificationEntity);
        return certificationMapping.certificationToDto(savedCertification);
    }

    public List<CertificationDto> getAllCertifications() {
        List<CertificationEntity> certifications = certificationRepository.findAll();
        return certificationMapping.certificationListToDtoList(certifications);
    }

    public Optional<CertificationDto> getCertificationById(Integer id) {
        CertificationEntity certificationEntity = certificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Certification with ID " + id + " does not exist."));
        return Optional.of(certificationMapping.certificationToDto(certificationEntity));
    }

    public Optional<CertificationDto> updateCertification(CertificationDto certificationDto) {
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
        return Optional.of(certificationMapping.certificationToDto(updatedCertification));
    }
}
