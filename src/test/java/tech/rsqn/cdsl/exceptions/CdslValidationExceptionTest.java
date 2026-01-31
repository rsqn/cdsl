package tech.rsqn.cdsl.exceptions;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CdslValidationExceptionTest {

    @Test
    public void shouldCreateExceptionWithDefaultConstructor() {
        CdslValidationException exception = new CdslValidationException();
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void shouldCreateExceptionWithMessage() {
        String message = "Validation failed";
        CdslValidationException exception = new CdslValidationException(message);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertNull(exception.getCause());
    }

    @Test
    public void shouldCreateExceptionWithMessageAndCause() {
        String message = "Validation failed";
        Throwable cause = new IllegalArgumentException("Invalid input");
        CdslValidationException exception = new CdslValidationException(message, cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getCause(), cause);
    }

    @Test
    public void shouldCreateExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid input");
        CdslValidationException exception = new CdslValidationException(cause);
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getCause(), cause);
        Assert.assertTrue(exception.getMessage().contains("IllegalArgumentException"));
    }

    @Test
    public void shouldCreateExceptionWithAllParameters() {
        String message = "Validation failed";
        Throwable cause = new IllegalArgumentException("Invalid input");
        boolean enableSuppression = true;
        boolean writableStackTrace = true;
        
        // Using reflection to access protected constructor
        CdslValidationException exception = new CdslValidationException(message, cause, enableSuppression, writableStackTrace) {};
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getMessage(), message);
        Assert.assertEquals(exception.getCause(), cause);
    }

    @Test
    public void shouldExtendCdslException() {
        CdslValidationException exception = new CdslValidationException("Test");
        Assert.assertTrue(exception instanceof CdslException);
        Assert.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void shouldPreserveStackTrace() {
        CdslValidationException exception = new CdslValidationException("Validation error");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        Assert.assertNotNull(stackTrace);
        Assert.assertTrue(stackTrace.length > 0);
    }

    @Test
    public void shouldHandleNullMessage() {
        CdslValidationException exception = new CdslValidationException((String) null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getMessage());
    }

    @Test
    public void shouldHandleNullCause() {
        CdslValidationException exception = new CdslValidationException((Throwable) null);
        Assert.assertNotNull(exception);
        Assert.assertNull(exception.getCause());
    }
}