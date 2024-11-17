package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for customer")
public class CustomerDto {
    @Schema(description = "Customer ID", example = "1")
    private Integer customerId;

    @Schema(description = "Name of the customer", example = "AS Merko Ehitus Eesti")
    private String name;

    @Schema(description = "Address of the customer", example = "123 Elm Street")
    private String address;

    @Schema(description = "City or county of the customer", example = "Tallinn")
    private String cityCounty;

    @Schema(description = "ZIP code of the customer", example = "10115")
    private String zip;

    @Schema(description = "Email of the customer", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Phone number of the customer", example = "+37212345678")
    private String phoneNumber;

    @Schema(description = "VAT number of the customer", example = "EE123456789")
    private String vatNo;
}
