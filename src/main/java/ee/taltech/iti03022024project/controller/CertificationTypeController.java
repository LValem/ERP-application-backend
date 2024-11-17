package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.CertificationTypeDto;
import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.service.CertificationTypeService;
import ee.taltech.iti03022024project.dto.searchcriteria.CertificationTypeSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/certification-types")
@Tag(name = "Certification Types", description = "APIs for managing certification types")
public class CertificationTypeController {

    private final CertificationTypeService certificationTypeService;

    @Operation(
            summary = "Add a new certification type",
            description = "Creates a new certification type with a unique name."
    )
    @ApiResponse(responseCode = "200", description = "Certification type added successfully")
    @ApiResponse(responseCode = "409", description = "Certification type with this name already exists")
    @PreAuthorize("hasAuthority('DEFAULT USER')")
    @PostMapping
    public ResponseEntity<CertificationTypeDto> createCertificationType(@RequestBody CertificationTypeDto certificationTypeDto) {
        CertificationTypeDto createdType = certificationTypeService.createCertificationType(certificationTypeDto);
        return ResponseEntity.ok(createdType);
    }

    @Operation(
            summary = "Get all certification types",
            description = "Fetches all certification types and returns a list of DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Certification types retrieved successfully")
    @PreAuthorize("hasAuthority('DEFAULT USER')")
    @GetMapping
    public ResponseEntity<List<CertificationTypeDto>> getAllCertificationTypes() {
        return ResponseEntity.ok(certificationTypeService.getAllCertificationTypes());
    }

    @Operation(
            summary = "Get a certification type by ID",
            description = "Retrieves a certification type by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Certification type retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Certification type with this ID does not exist")
    @PreAuthorize("hasAuthority('DEFAULT USER')")
    @GetMapping("/{id}")
    public ResponseEntity<CertificationTypeDto> getCertificationTypeById(@PathVariable Integer id) {
        return certificationTypeService.getCertificationTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update a certification type",
            description = "Updates the name of an existing certification type by its ID."
    )
    @ApiResponse(responseCode = "200", description = "Certification type updated successfully")
    @ApiResponse(responseCode = "404", description = "Certification type with this ID does not exist")
    @ApiResponse(responseCode = "409", description = "Certification type with the new name already exists")
    @PreAuthorize("hasAuthority('DEFAULT USER')")
    @PutMapping("/{id}")
    public ResponseEntity<CertificationTypeDto> updateCertificationType(@PathVariable Integer id, @RequestBody CertificationTypeDto certificationTypeDto) {
        certificationTypeDto.setCertificationTypeId(id);
        return certificationTypeService.updateCertificationType(certificationTypeDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search for certification types",
            description = "Search certification types by criteria, sort, and return a paginated table view"
    )
    @ApiResponse(responseCode = "200", description = "Certification types retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no certification types")
    @PostMapping("/table")
    public ResponseEntity<PageResponse<CertificationTypeDto>> searchCertificationTypes(@Valid @RequestBody(required = false) CertificationTypeSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new CertificationTypeSearchCriteria();
        }
        PageResponse<CertificationTypeDto> response = certificationTypeService.searchCertificationTypes(criteria);
        return ResponseEntity.ok(response);
    }
}
