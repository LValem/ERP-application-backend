package ee.taltech.iti03022024project.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing orders information with customer name, pickup date. drop off date, weight, width, height, length and order details")
public class OrdersTableInfoDto {

    @Schema(description = "Order ID", example = "1")
    private Integer orderId;

    @Schema(description = "Name of the customer", example = "AS Merko Ehitus Eesti")
    private String customerName;

    @Schema(description = "Pick up date", example = "2024-12-01")
    private LocalDateTime pickupDate;

    @Schema(description = "Drop off date", example = "2024-12-01")
    private LocalDateTime dropOffDate;

    @Schema(description = "Weight of the goods in kg", example = "6000")
    private int weight;

    @Schema(description = "Width of the cargo in cm", example = "200")
    private int width;

    @Schema(description = "Height of the cargo in cm", example = "450")
    private int height;

    @Schema(description = "Length of the cargo in cm", example = "1500")
    private int length;

    @Schema(description = "Additional order details", example = "Address Kuu 10, Saku, in front of the house there are 3 big blocks of cement, each are 2000kg, the sizes are the same.")
    private String orderDetails;
}
