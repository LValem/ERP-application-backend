package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing fuel consumption details")
public class FuelConsumptionDto {

    @Schema(description = "Fuel Consumption ID", example = "1")
    private Integer fuelConsumptionId;

    @Schema(description = "Job ID associated with the fuel consumption", example = "101")
    private Integer jobId;

    @Schema(description = "Vehicle ID associated with the fuel consumption", example = "12")
    private Integer vehicleId;

    @Schema(description = "Amount of fuel used in liters", example = "50.5")
    private BigDecimal fuelUsed;

    @Schema(description = "Distance driven in kilometers", example = "120.0")
    private BigDecimal distanceDriven;

    @Schema(description = "Date and time of the fuel consumption record", example = "2024-11-10T08:00:00")
    private LocalDateTime consumptionDate;
}
