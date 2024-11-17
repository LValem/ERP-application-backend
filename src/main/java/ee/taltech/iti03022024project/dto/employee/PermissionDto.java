package ee.taltech.iti03022024project.dto.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for permission information")
public class PermissionDto {
    @Schema(description = "Description of the permission", example = "ADMIN")
    private String description;
}
