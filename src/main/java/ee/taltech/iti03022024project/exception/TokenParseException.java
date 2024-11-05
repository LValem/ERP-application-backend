package ee.taltech.iti03022024project.exception;

public class TokenParseException extends RuntimeException {
    public TokenParseException(String message, Throwable cause) {
        super(message, cause);
    }
}