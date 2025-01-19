package ee.taltech.iti03022024project.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object representing vehicle information")
public class VehicleTableInfoDto {
    @Schema(description = "Unique identifier for the vehicle", example = "123")
    private Integer vehicleId;

    @Schema(description = "Type of the vehicle (e.g., 'C' for Car, 'T' for Truck)", example = "C")
    private Character vehicleType;

    @Schema(description = "Indicates whether the vehicle is currently in use", example = "true")
    private Boolean isInUse;

    @Schema(description = "Maximum load capacity of the vehicle in kilograms", example = "2000")
    private Integer maxLoad;

    @Schema(description = "Current fuel level of the vehicle in liters", example = "50")
    private Integer currentFuel;

    @Schema(description = "Registration plate of the vehicle", example = "ABC-1234")
    private String registrationPlate;
}
