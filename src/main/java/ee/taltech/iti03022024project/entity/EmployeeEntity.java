package ee.taltech.iti03022024project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Reference;

@RequiredArgsConstructor
@Data
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeId;
    private String name;
    // @Reference kuidas tuleb viidata fkey puhul?
    // private Integer permissionId;
    @ManyToOne
    @JoinColumn(name = "permission_id", referencedColumnName = "permissionId")
    private PermissionEntity permission;
}
