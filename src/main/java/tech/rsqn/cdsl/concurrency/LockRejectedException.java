package tech.rsqn.cdsl.concurrency;

public class LockRejectedException extends RuntimeException {

    public LockRejectedException() {
        super();
    }

    public LockRejectedException(String message) {
        super(message);
    }

    public LockRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockRejectedException(Throwable cause) {
        super(cause);
    }

    protected LockRejectedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
