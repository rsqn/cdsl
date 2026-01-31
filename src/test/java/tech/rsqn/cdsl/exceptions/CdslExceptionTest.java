package tech.rsqn.cdsl.exceptions;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CdslExceptionTest {

    @Test
    public void shouldCreateExceptionWithDefaultConstructor() {
        CdslException exception = new CdslException();
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void shouldCreateExceptionWithMessage() {
        String message = "Test error message";
        CdslException exception = new CdslException(message);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void shouldCreateExceptionWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        CdslException exception = new CdslException(message, cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getCause(), cause);
    }

    @Test
    public void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        CdslException exception = new CdslException(cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertTrue(exception.getMessage().contains("RuntimeException"));
    }

    @Test
    public void shouldCreateExceptionWithAllParameters() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");
        boolean enableSuppression = true;
        boolean writableStackTrace = true;
        
        // Using reflection to access protected constructor
        CdslException exception = new CdslException(message, cause, enableSuppression, writableStackTrace) {};
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getCause(), cause);
    }

    @Test
    public void shouldBeRuntimeException() {
        CdslException exception = new CdslException("Test");
        Assert.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void shouldPreserveStackTrace() {
        CdslException exception = new CdslException("Test message");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        Assert.assertNotNull(stackTrace);
        Assert.assertTrue(stackTrace.length > 0);
    }
}