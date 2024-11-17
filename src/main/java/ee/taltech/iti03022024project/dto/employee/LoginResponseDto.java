package ee.taltech.iti03022024project.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;


@AllArgsConstructor
@Data
@Schema(description = "Response DTO containing the authentication token")
public class LoginResponseDto {

    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
