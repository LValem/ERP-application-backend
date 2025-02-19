package ee.taltech.iti03022024project.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for employee information")
public class EmployeeDto {
    @Schema(description = "Name of the employee", example = "Lennart Valem")
    private String name;
    @Schema(description = "id of employee", example = "1")
    private Integer employeeId;
    @Schema(description = "Permission Id of employee", example = "1 == ADMIN")
    private Integer permissionId;
}
