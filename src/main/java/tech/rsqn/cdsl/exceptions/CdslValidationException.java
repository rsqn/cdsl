package tech.rsqn.cdsl.exceptions;

public class CdslValidationException extends CdslException {

    public CdslValidationException() {
        super();
    }

    public CdslValidationException(String message) {
        super(message);
    }

    public CdslValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CdslValidationException(Throwable cause) {
        super(cause);
    }

    protected CdslValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
