package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "certification_type")
public class CertificationTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer certificationTypeId;

    @Setter
    @Column(nullable = false, length = 100)
    private String certificationName;
}
