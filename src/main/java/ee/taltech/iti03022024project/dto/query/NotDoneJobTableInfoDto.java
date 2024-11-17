package ee.taltech.iti03022024project.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing not completed job's information, including vehicle, order, customer, and job completion details")
public class NotDoneJobTableInfoDto {

    @Schema(description = "Job ID", example = "1")
    private Integer jobId;

    @Schema(description = "Vehicle ID associated with the job", example = "12")
    private Integer vehicleId;

    @Schema(description = "Registration plate of the vehicle", example = "ABC123")
    private String registrationPlate;

    @Schema(description = "Order ID associated with the job", example = "101")
    private Integer orderId;

    @Schema(description = "Name of the customer for the order", example = "AS Merko Ehitus Eesti")
    private String customerName;

    @Schema(description = "Pickup date and time for the job", example = "2024-11-10T08:00:00")
    private LocalDateTime pickupDate;

    @Schema(description = "Drop-off date and time for the job", example = "2024-11-10T17:00:00")
    private LocalDateTime dropOffDate;

    @Schema(description = "Indicates whether the job is complete", example = "true")
    private boolean isComplete;
}
