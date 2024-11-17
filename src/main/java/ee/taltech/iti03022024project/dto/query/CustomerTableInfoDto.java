package ee.taltech.iti03022024project.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing customer information with address, contact details, and last order date")
public class CustomerTableInfoDto {

    @Schema(description = "Customer ID", example = "1")
    private Integer customerId;

    @Schema(description = "Name of the customer", example = "AS Merko Ehitus Eesti")
    private String customerName;

    @Schema(description = "Address of the customer", example = "123 Main Street")
    private String address;

    @Schema(description = "City or county of the customer", example = "Tallinn")
    private String cityCounty;

    @Schema(description = "ZIP code of the customer's address", example = "10111")
    private String zip;

    @Schema(description = "Email address of the customer", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the customer", example = "+3725551234")
    private String phoneNumber;

    @Schema(description = "VAT number of the customer", example = "EE123456789")
    private String vatNo;

    @Schema(description = "The date of the customer's last order", example = "2024-11-10T17:00:00")
    private LocalDateTime lastOrderDate;
}
