package ee.taltech.iti03022024project.exception;

public class CustomAccessDeniedException extends RuntimeException {

    public CustomAccessDeniedException() {
        super("Access denied: You do not have permission to access this resource");
    }

    public CustomAccessDeniedException(String message) {
        super(message);
    }
}
