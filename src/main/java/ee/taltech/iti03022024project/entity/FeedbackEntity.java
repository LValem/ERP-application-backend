package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "feedback")
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer difficulty;
    private Integer studymaterial_quality;
    private Integer involvement;
    private Integer course_structure;
    private Integer teamwork;
    private Integer outcome;
}
