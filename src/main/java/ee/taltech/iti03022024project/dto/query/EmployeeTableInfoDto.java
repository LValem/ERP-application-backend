package ee.taltech.iti03022024project.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing employee information with permission, multiple certifications, and last job date")
public class EmployeeTableInfoDto {

    @Schema(description = "Employee id", example = "1")
    private Integer employeeId;

    @Schema(description = "Name of the employee", example = "John Doe")
    private String employeeName;

    @Schema(description = "Description of the permission associated with the employee", example = "DEFAULT USER")
    private String permissionDescription;

    @Schema(description = "List of certification names under the certification type", example = "['B', 'CE']")
    private String certificationNames;

    @Schema(description = "The date when the last job was completed by the employee", example = "2024-11-10")
    private LocalDateTime lastJobDate;
}
