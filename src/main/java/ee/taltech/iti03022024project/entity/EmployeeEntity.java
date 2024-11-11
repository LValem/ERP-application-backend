package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class EmployeeEntity {

    public EmployeeEntity(Integer employeeId, String name, Integer permissionId, String password) {
        this.employeeId = employeeId;
        this.name = name;
        this.permissionId = permissionId;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;
    private String name;
    private Integer permissionId;
    private String password;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    private List<CertificationEntity> certifications;
}
