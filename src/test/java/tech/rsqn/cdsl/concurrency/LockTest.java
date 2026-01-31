package tech.rsqn.cdsl.concurrency;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LockTest {

    @Test
    public void shouldCreateInstanceWithNullValues() {
        Lock lock = new Lock();
        Assert.assertNull(lock.getId());
        Assert.assertNull(lock.getGrantee());
        Assert.assertNull(lock.getGrantedResource());
        Assert.assertEquals(lock.getGrantedMs(), 0);
        Assert.assertEquals(lock.getExpiresMs(), 0);
    }

    @Test
    public void shouldSetAndGetValues() {
        Lock lock = new Lock();
        lock.setId("testId");
        lock.setGrantee("testGrantee");
        lock.setGrantedResource("testResource");
        lock.setGrantedMs(1000);
        lock.setExpiresMs(2000);

        Assert.assertEquals(lock.getId(), "testId");
        Assert.assertEquals(lock.getGrantee(), "testGrantee");
        Assert.assertEquals(lock.getGrantedResource(), "testResource");
        Assert.assertEquals(lock.getGrantedMs(), 1000);
        Assert.assertEquals(lock.getExpiresMs(), 2000);
    }

    @Test
    public void shouldCheckExpiration() {
        Lock lock = new Lock();
        lock.setExpiresMs(System.currentTimeMillis() + 1000);
        Assert.assertFalse(lock.hasExpired());

        lock.setExpiresMs(System.currentTimeMillis() - 1000);
        Assert.assertTrue(lock.hasExpired());
    }
}