package ee.taltech.iti03022024project.controller;

import ee.taltech.iti03022024project.dto.UpdateVehicleRequestDto;
import ee.taltech.iti03022024project.dto.VehicleDto;
import ee.taltech.iti03022024project.service.VehicleService;
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
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle management APIs")
public class VehicleController {

    private final VehicleService vehicleService;

    @Operation(
            summary = "Add a new vehicle to the system",
            description = "Creates a new vehicle based on the provided details."
    )
    @ApiResponse(responseCode = "200", description = "Vehicle added successfully")
    @ApiResponse(responseCode = "409", description = "Vehicle with this registration plate already exists")
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping("/update/{id}")
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

}
