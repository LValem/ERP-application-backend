package ee.taltech.iti03022024project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedbackDto {
    private String name;
    private Integer difficulty;
    private Integer studymaterial_quality;
    private Integer involvement;
    private Integer course_structure;
    private Integer teamwork;
    private Integer outcome;
}
