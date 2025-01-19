package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.PageResponse;
import ee.taltech.iti03022024project.dto.UpdateVehicleRequestDto;
import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.dto.query.VehicleTableInfoDto;
import ee.taltech.iti03022024project.dto.searchcriteria.VehicleSearchCriteria;
import ee.taltech.iti03022024project.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle management APIs")
public class VehicleController {

    private final VehicleService vehicleService;
    private static final Logger log = LoggerFactory.getLogger(VehicleController.class);

    @Operation(
            summary = "Add a new vehicle to the system",
            description = "Creates a new vehicle based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Vehicle added successfully")
    @ApiResponse(responseCode = "409", description = "Vehicle with this registration plate already exists")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(@RequestBody VehicleDto vehicleDto) {
        VehicleDto createdVehicle = vehicleService.createVehicle(vehicleDto);
        return ResponseEntity.ok().body(createdVehicle);
    }

    @Operation(
            summary = "Get all vehicles from the system",
            description = "Fetches all vehicles and returns a list of vehicle DTOs."
    )
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "404", description = "There are no vehicles")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'DRIVER')")
    @GetMapping
    public ResponseEntity<List<VehicleDto>> getVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @Operation(
            summary = "Get a vehicle by ID",
            description = "Retrieves a vehicle by its ID and returns the corresponding vehicle DTO."
    )
    @ApiResponse(responseCode = "200", description = "Vehicle retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle with this ID does not exist")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'DRIVER')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicle(@PathVariable Integer id) {
        return vehicleService.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update vehicle information",
            description = "Allows updating vehicle details such as type, status, load, fuel, and registration plate."
    )
    @ApiResponse(responseCode = "200", description = "Vehicle updated successfully")
    @ApiResponse(responseCode = "404", description = "Vehicle with this ID does not exist")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @ApiResponse(responseCode = "409", description = "Invalid input")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Integer id,
            @RequestBody UpdateVehicleRequestDto updateVehicleRequestDto
    ) {
        return vehicleService.updateVehicle(id, updateVehicleRequestDto.getVehicleType(),
                        updateVehicleRequestDto.getIsInUse(), updateVehicleRequestDto.getMaxLoad(),
                        updateVehicleRequestDto.getCurrentFuel(),
                        updateVehicleRequestDto.getRegistrationPlate())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Search for vehicles in table view",
            description = "Search vehicles by criteria, sort, and return a paginated table view."
    )
    @ApiResponse(responseCode = "200", description = "Vehicles retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User doesn't have correct permissions!")
    @ApiResponse(responseCode = "404", description = "There are no vehicles")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'DRIVER')")
    @GetMapping("/table")
    public ResponseEntity<PageResponse<VehicleTableInfoDto>> searchVehicles(@Valid VehicleSearchCriteria criteria) {
        if (criteria == null) {
            criteria = new VehicleSearchCriteria();
        }
        PageResponse<VehicleTableInfoDto> response = vehicleService.searchVehicleTable(criteria);
        return ResponseEntity.ok(response);
    }

}
