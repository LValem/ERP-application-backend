package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for order details, when creating a new job.")
public class OrderNameIdDto {
    @Schema(description = "Order ID", example = "1")
    private Integer orderId;

    @Schema(description = "Customer ID associated with the order", example = "3")
    private Integer customerId;

    @Schema(description = "Date and time for the pickup", example = "2024-11-11T09:00:00")
    private String customerName;
}
