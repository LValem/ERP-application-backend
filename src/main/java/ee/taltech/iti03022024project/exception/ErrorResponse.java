package ee.taltech.iti03022024project.exception;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class ErrorResponse {

    private String message;
}
