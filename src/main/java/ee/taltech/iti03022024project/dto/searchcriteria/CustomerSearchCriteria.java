package ee.taltech.iti03022024project.dto.searchcriteria;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerSearchCriteria {

    @PositiveOrZero
    private Integer customerId;

    @Size(min = 1, max = 100)
    private String customerName;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String cityCounty;

    @Pattern(regexp = "^[0-9]{5}$", message = "ZIP code must be a 5-digit number")
    private String zip;

    @Email
    @Size(max = 100)
    private String email;

    @Pattern(regexp = "^[+]*[0-9]{7,15}$", message = "Phone number must be between 7 and 15 digits and can include a '+' at the beginning")
    private String phoneNumber;

    @Size(max = 64)
    private String vatNo;

    private LocalDateTime lastOrderStartDate;
    private LocalDateTime lastOrderEndDate;

    // Pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
