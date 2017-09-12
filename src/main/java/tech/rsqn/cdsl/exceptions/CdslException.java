package tech.rsqn.cdsl.exceptions;

public class CdslException extends RuntimeException {

    public CdslException() {
        super();
    }

    public CdslException(String message) {
        super(message);
    }

    public CdslException(String message, Throwable cause) {
        super(message, cause);
    }

    public CdslException(Throwable cause) {
        super(cause);
    }

    protected CdslException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
