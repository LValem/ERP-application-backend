package ee.taltech.iti03022024project.exception;

public class WrongValueException extends RuntimeException{
    public WrongValueException(String message) {
        super(message);
    }
}
