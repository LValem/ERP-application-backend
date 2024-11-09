package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for CreateUserDto information")
public class CreateEmployeeDto {
    @Schema(description = "Name of user that is trying to login", example = "Lennart Valem")
    private String name;
    @Schema(description = "password of user", example = "xxxxxxx")
    private String password;
}