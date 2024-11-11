package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "certification")
public class CertificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer certificationId;

    @ManyToOne
    @JoinColumn(name = "certification_type_id", nullable = false)
    private CertificationTypeEntity certificationType;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employee;

    private LocalDate issuedDate;
    private LocalDate expiryDate;
}
