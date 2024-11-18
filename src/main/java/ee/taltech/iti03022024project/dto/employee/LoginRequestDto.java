package ee.taltech.iti03022024project.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Data Transfer Object for loginRequest information")
public class LoginRequestDto {
    @Schema(description = "Name of user that is trying to login", example = "Lennart Valem")
    private String name;
    @Schema(description = "password of user", example = "xxxxxxx")
    private String password;
}
