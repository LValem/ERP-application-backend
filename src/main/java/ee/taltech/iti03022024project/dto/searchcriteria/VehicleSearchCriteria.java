package ee.taltech.iti03022024project.dto.searchcriteria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VehicleSearchCriteria {

    @PositiveOrZero
    private Integer vehicleId;

    @Size(min = 1, max = 1)
    private Character vehicleType;

    @NotNull
    private Boolean isInUse;

    @Size(min = 1, max = 100000)
    private Integer minimumLoad;

    @Size(min = 1, max = 100000)
    private Integer maximumLoad;

    @Size(min = 1, max = 1000)
    private Integer minFuel;

    @Size(min = 1, max = 1000)
    private Integer maxFuel;

    @Size(min = 1, max = 10)
    private String registrationPlate;

    //pagination and sorting
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
