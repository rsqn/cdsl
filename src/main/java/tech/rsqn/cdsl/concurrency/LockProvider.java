package tech.rsqn.cdsl.concurrency;

public interface LockProvider {
    Lock obtain(String grantee, String resource, long durationMs, int retries, long retryDelayMs) throws LockRejectedException;

    void release(Lock lock) throws LockRejectedException;

}
