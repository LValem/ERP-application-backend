package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for vehicle information")
public class VehicleDto {
    @Schema(description = "Type of the vehicle", example = "K")
    private Character vehicleType;
    @Schema(description = "Indicates if the vehicle is in use", example = "False")
    private Boolean isInUse;
    @Schema(description = "Maximum load capacity of the vehicle in kilograms", example = "30000")
    private Integer maxLoad;
    @Schema(description = "Current fuel level of the vehicle in liters", example = "100")
    private Integer currentFuel;
    @Schema(description = "Registration plate number", example = "648MKC")
    private String registrationPlate;
}
