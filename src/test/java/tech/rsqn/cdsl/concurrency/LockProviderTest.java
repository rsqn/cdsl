package tech.rsqn.cdsl.concurrency;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LockProviderTest {

    @Test
    public void shouldObtainLockSuccessfully() {
        LockProvider provider = new MockLockProvider();
        try {
            Lock lock = provider.obtain("testUser", "testResource", 1000, 3, 100);
            Assert.assertNotNull(lock);
            Assert.assertEquals(lock.getGrantee(), "testUser");
            Assert.assertEquals(lock.getGrantedResource(), "testResource");
        } catch (LockRejectedException e) {
            Assert.fail("Should not throw LockRejectedException for successful lock");
        }
    }

    @Test(expectedExceptions = LockRejectedException.class)
    public void shouldThrowExceptionWhenLockRejected() throws LockRejectedException {
        LockProvider provider = new MockLockProvider();
        provider.obtain("testUser", "unavailableResource", 1000, 3, 100);
    }

    @Test
    public void shouldReleaseLockSuccessfully() {
        LockProvider provider = new MockLockProvider();
        try {
            Lock lock = provider.obtain("testUser", "testResource", 1000, 3, 100);
            provider.release(lock);
            Assert.assertTrue(true); // If no exception, test passes
        } catch (LockRejectedException e) {
            Assert.fail("Should not throw LockRejectedException for successful release");
        }
    }

    @Test(expectedExceptions = LockRejectedException.class)
    public void shouldThrowExceptionWhenReleasingInvalidLock() throws LockRejectedException {
        LockProvider provider = new MockLockProvider();
        Lock invalidLock = new Lock();
        invalidLock.setId("invalidId");
        provider.release(invalidLock);
    }

    private static class MockLockProvider implements LockProvider {
        @Override
        public Lock obtain(String grantee, String resource, long durationMs, int retries, long retryDelayMs) throws LockRejectedException {
            if ("unavailableResource".equals(resource)) {
                throw new LockRejectedException("Resource unavailable");
            }
            Lock lock = new Lock();
            lock.setId("testLockId");
            lock.setGrantee(grantee);
            lock.setGrantedResource(resource);
            lock.setGrantedMs(System.currentTimeMillis());
            lock.setExpiresMs(System.currentTimeMillis() + durationMs);
            return lock;
        }

        @Override
        public void release(Lock lock) throws LockRejectedException {
            if (lock == null || "invalidId".equals(lock.getId())) {
                throw new LockRejectedException("Invalid lock");
            }
        }
    }
}