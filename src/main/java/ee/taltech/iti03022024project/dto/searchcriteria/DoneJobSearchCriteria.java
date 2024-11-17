package ee.taltech.iti03022024project.dto.searchcriteria;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DoneJobSearchCriteria {

    @PositiveOrZero
    private Integer jobId;

    @PositiveOrZero
    private Integer vehicleId;

    @Size(max = 20)
    private String registrationPlate;

    @PositiveOrZero
    private Double minFuelUsed;

    @PositiveOrZero
    private Double maxFuelUsed;

    @PositiveOrZero
    private Double minDistanceDriven;

    @PositiveOrZero
    private Double maxDistanceDriven;

    @PositiveOrZero
    private Integer orderId;

    @Size(min = 1, max = 100)
    private String customerName;

    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;
    private LocalDateTime dropOffStartDate;
    private LocalDateTime dropOffEndDate;

    // Pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
