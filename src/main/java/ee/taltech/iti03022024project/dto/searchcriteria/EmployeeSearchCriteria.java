package ee.taltech.iti03022024project.dto.searchcriteria;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeSearchCriteria {

    @PositiveOrZero
    private Integer employeeId;

    @Size(min = 1, max = 100)
    private String employeeName;

    @Size(max = 255)
    private String permissionDescription;

    @Size(max = 100)
    private String certificationNames;

    private LocalDateTime lastJobStartDate;
    private LocalDateTime lastJobEndDate;

    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
