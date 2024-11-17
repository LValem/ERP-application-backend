package ee.taltech.iti03022024project.dto.searchcriteria;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CertificationTypeSearchCriteria {

    @PositiveOrZero
    private Integer certificationTypeId;
    private String certificationName;

    // Pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
