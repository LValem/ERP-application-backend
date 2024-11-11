package ee.taltech.iti03022024project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Data Transfer Object for certification")
public class CertificationDto {
    @Schema(description = "Certification id", example = "1")
    private Integer certificationId;
    @Schema(description = "Employee id", example = "7")
    private Integer employeeId;
    @Schema(description = "certification type id", example = "1")
    private Integer certificationTypeId;
    @Schema(description = "Date when the certification was issued", example = "2022-12-02")
    private LocalDate issuedDate;
    @Schema(description = "Date when the certification will expire", example = "2032-12-02")
    private LocalDate expiryDate;
}
