package ee.taltech.iti03022024project.dto.searchcriteria;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSearchCriteria {
    private Integer orderId;
    private String customerName;
    private LocalDateTime pickupStartDate;
    private LocalDateTime pickupEndDate;
    private LocalDateTime dropOffStartDate;
    private LocalDateTime dropOffEndDate;
    private Integer minWeight;
    private Integer maxWeight;
    private Integer minLength;
    private Integer maxLength;
    private Integer minWidth;
    private Integer maxWidth;
    private Integer minHeight;
    private Integer maxHeight;

    // Pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
