package ee.taltech.iti03022024project.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Employee update fields")
public class UpdateEmployeeRequest {

    @Schema(description = "Full name of the employee", example = "John Doe")
    private String name;

    @Schema(description = "Permission level of the employee", example = "1")
    private Integer permissionId;

    @Schema(description = "Password for the employee account", example = "securePassword123")
    private String password;
}
