package tech.rsqn.cdsl.annotations;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CdslDefTest {

    @Test
    public void shouldRetrieveValueFromAnnotation() {
        CdslDef annotation = TestClassWithDef.class.getAnnotation(CdslDef.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(annotation.value(), "testValue");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowExceptionWhenAnnotationMissing() {
        CdslDef annotation = TestClassWithoutDef.class.getAnnotation(CdslDef.class);
        Assert.assertNull(annotation);
        // This should throw NullPointerException when trying to access annotation properties
        annotation.value();
    }

    @CdslDef("testValue")
    private static class TestClassWithDef {}

    private static class TestClassWithoutDef {}
}