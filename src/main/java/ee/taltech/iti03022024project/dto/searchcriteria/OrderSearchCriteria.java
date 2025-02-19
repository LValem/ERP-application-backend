package ee.taltech.iti03022024project.dto.searchcriteria;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSearchCriteria {

    @PositiveOrZero
    private Integer orderId;

    @Size(min = 1, max = 100)
    private String customerName;

    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;
    private LocalDateTime dropOffStartDate;
    private LocalDateTime dropOffEndDate;

    @PositiveOrZero
    private Integer minWeight;

    @PositiveOrZero
    private Integer maxWeight;

    @PositiveOrZero
    private Integer minLength;

    @PositiveOrZero
    private Integer maxLength;

    @PositiveOrZero
    private Integer minWidth;

    @PositiveOrZero
    private Integer maxWidth;

    @PositiveOrZero
    private Integer minHeight;

    @PositiveOrZero
    private Integer maxHeight;

    // Pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
