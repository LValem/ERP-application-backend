package ee.taltech.iti03022024project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VehicleDto {
    private Character vehicleType;
    private Boolean isInUse;
    private Integer maxLoad;
    private Integer currentFuel;
    private String registrationPlate;
}
