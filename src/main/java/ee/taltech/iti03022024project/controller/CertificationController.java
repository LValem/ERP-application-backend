package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CertificationDto;
import ee.taltech.iti03022024project.service.CertificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/certifications")
@Tag(name = "Certifications", description = "APIs for managing certifications")
public class CertificationController {

    private final CertificationService certificationService;

    @Operation(
            summary = "Create a new certification",
            description = "Adds a new certification for an employee based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Certification created successfully")
    @ApiResponse(responseCode = "404", description = "Certification type or employee not found")
    @ApiResponse(responseCode = "409", description = "Certification with this type already exists for the employee")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<CertificationDto> createCertification(@RequestBody CertificationDto certificationDto) {
        CertificationDto createdCertification = certificationService.createCertification(certificationDto);
        return ResponseEntity.ok(createdCertification);
    }

    @Operation(
            summary = "Get all certifications",
            description = "Fetches all certifications and returns a list of certification DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Certifications retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<List<CertificationDto>> getAllCertifications() {
        List<CertificationDto> certifications = certificationService.getAllCertifications();
        return ResponseEntity.ok(certifications);
    }

    @Operation(
            summary = "Get a certification by ID",
            description = "Retrieves a certification by its ID and returns the corresponding DTO."
    )
    @ApiResponse(responseCode = "200", description = "Certification retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Certification with this ID does not exist")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CertificationDto> getCertificationById(@PathVariable Integer id) {
        return certificationService.getCertificationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing certification",
            description = "Updates the certification type and dates of an existing certification."
    )
    @ApiResponse(responseCode = "200", description = "Certification updated successfully")
    @ApiResponse(responseCode = "404", description = "Certification or certification type not found")
    @ApiResponse(responseCode = "409", description = "Duplicate certification with the same type already exists")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<CertificationDto> updateCertification(@PathVariable Integer id, @RequestBody CertificationDto certificationDto) {
        certificationDto.setCertificationId(id);
        return certificationService.updateCertification(certificationDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
