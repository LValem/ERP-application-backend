package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for certification type")
public class CertificationTypeDto {
    @Schema(description = "Certification type id", example = "1")
    private Integer certificationTypeId;

    @Schema(description = "Name of the certification type", example = "B")
    private String certificationName;
}
