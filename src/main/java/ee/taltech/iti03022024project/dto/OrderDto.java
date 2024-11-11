package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for order")
public class OrderDto {
    @Schema(description = "Order ID", example = "1")
    private Integer orderId;

    @Schema(description = "Customer ID associated with the order", example = "3")
    private Integer customerId;

    @Schema(description = "Date and time for the pickup", example = "2024-11-11T09:00:00")
    private LocalDateTime pickupDate;

    @Schema(description = "Date and time for the drop-off", example = "2024-11-11T18:00:00")
    private LocalDateTime dropOffDate;

    @Schema(description = "Weight of the order in kilograms", example = "12000")
    private Integer weight;

    @Schema(description = "Width of the order in centimeters", example = "200")
    private Integer width;

    @Schema(description = "Height of the order in centimeters", example = "300")
    private Integer height;

    @Schema(description = "Length of the order in centimeters", example = "200")
    private Integer length;

    @Schema(description = "Additional details about the order", example = "Fragile items, handle with care")
    private String orderDetails;
}
