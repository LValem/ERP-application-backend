package ee.taltech.iti03022024project.dto.employee;

import lombok.Data;

@Data
public class UpdateEmployeeRequest {
    private String name;
    private Integer permissionID;
    private String password;
}
