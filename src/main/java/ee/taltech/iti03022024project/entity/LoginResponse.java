package ee.taltech.iti03022024project.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class LoginResponse {
    private String token;
}
