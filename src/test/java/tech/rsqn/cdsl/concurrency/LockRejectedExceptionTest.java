package tech.rsqn.cdsl.concurrency;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LockRejectedExceptionTest {

    @Test
    public void shouldCreateInstanceWithDefaultConstructor() {
        LockRejectedException exception = new LockRejectedException();
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void shouldCreateInstanceWithMessage() {
        String message = "Test message";
        LockRejectedException exception = new LockRejectedException(message);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void shouldCreateInstanceWithCause() {
        Throwable cause = new RuntimeException("Test cause");
        LockRejectedException exception = new LockRejectedException(cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertEquals(exception.getMessage(), cause.toString());
    }

    @Test
    public void shouldCreateInstanceWithMessageAndCause() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");
        LockRejectedException exception = new LockRejectedException(message, cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getCause(), cause);
    }

    @Test
    public void shouldCreateInstanceWithAllParameters() {
        String message = "Test message";
        Throwable cause = new RuntimeException("Test cause");
        LockRejectedException exception = new LockRejectedException(message, cause, true, true);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getCause(), cause);
    }
}