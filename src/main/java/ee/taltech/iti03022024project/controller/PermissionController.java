package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.PermissionDto;
import ee.taltech.iti03022024project.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permissions", description = "Permission management APIs")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(
            summary = "Add a new permission to the system",
            description = "Creates a new permission based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Permission added successfully")
    @ApiResponse(responseCode = "409", description = "Permission with this description already exists")
    @PostMapping
    public ResponseEntity<PermissionDto> createPermission(@RequestBody PermissionDto permissionDto) {
        PermissionDto createdPermission = permissionService.createPermission(permissionDto);
        return ResponseEntity.ok(createdPermission);
    }

    @Operation(
            summary = "Get all permissions from the system",
            description = "Fetches all permissions and returns a list of permission DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no permissions")
    @GetMapping
    public ResponseEntity<List<PermissionDto>> getPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @Operation(
            summary = "Get a permission by ID",
            description = "Retrieves a permission by its ID and returns the corresponding permission DTO."
    )
    @ApiResponse(responseCode = "200", description = "Permission retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Permission with this ID does not exist")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionDto> getPermission(@PathVariable Integer id) {
        return permissionService.getPermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
