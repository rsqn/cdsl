package tech.rsqn.cdsl.concurrency;

import tech.rsqn.useful.things.identifiers.UIDHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * This is for unit tests - it is not thread safe
 */
public class LockProviderUnitTestSupport implements LockProvider {
    protected Map<String, Lock> locks = new HashMap<>();

    private void doSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }
    }

    /**
     * This is not threadsafe
     * @param grantee
     * @param resource
     * @param durationMs
     * @param retries
     * @param retryDelay
     * @return
     * @throws LockRejectedException
     */
    public Lock obtain(String grantee, String resource, long durationMs, int retries, long retryDelay) throws LockRejectedException {
        Lock lock = null;

        boolean grantLock = false;

        for (int i = 0; i < retries; i++) {
            lock = locks.get(resource);
            if (lock != null) {
                if (!lock.hasExpired()) {
                    doSleep(retryDelay);
                } else {
                    grantLock = true;
                }
            } else {
                grantLock = true;
            }

            if (grantLock) {
                lock = new Lock();
                lock.setGrantedResource(resource);
                lock.setGrantee(grantee);
                lock.setGrantedMs(System.currentTimeMillis());
                lock.setExpiresMs(lock.getGrantedMs() + durationMs);
                lock.setId(UIDHelper.generate());
                locks.put(lock.getGrantedResource(), lock);
                return lock;
            }
        }

        throw new LockRejectedException("Lock request on " + resource + " for " + grantee + " rejected after " + retries + " attempts");
    }

    @Override
    public void release(Lock lock) throws LockRejectedException {
        locks.remove(lock.getGrantedResource());
    }
}
