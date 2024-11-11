package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for job")
public class JobDto {
    @Schema(description = "Job id", example = "1")
    private Integer jobId;

    @Schema(description = "Vehicle id associated with the job", example = "5")
    private Integer vehicleId;

    @Schema(description = "Employee id associated with the job", example = "7")
    private Integer employeeId;

    @Schema(description = "Order id associated with the job", example = "12")
    private Integer orderId;

    @Schema(description = "Date and time when the pickup is scheduled", example = "2024-11-11T09:00:00")
    private LocalDateTime pickupDate;

    @Schema(description = "Date and time when the drop-off is scheduled", example = "2024-11-11T18:00:00")
    private LocalDateTime dropOffDate;

    @Schema(description = "Indicates if the job is complete", example = "false")
    private Boolean isComplete;
}
