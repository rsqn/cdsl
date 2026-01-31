package tech.rsqn.cdsl.annotations;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CdslAttributesTest {

    @Test
    public void shouldRetrieveNamesFromAnnotation() {
        CdslAttributes annotation = TestClassWithAttributes.class.getAnnotation(CdslAttributes.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(annotation.names().length, 2);
        Assert.assertEquals(annotation.names()[0], "testName1");
        Assert.assertEquals(annotation.names()[1], "testName2");
    }

    @Test
    public void shouldHandleEmptyNamesArray() {
        CdslAttributes annotation = TestClassWithEmptyNames.class.getAnnotation(CdslAttributes.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(annotation.names().length, 0);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowExceptionWhenAnnotationMissing() {
        CdslAttributes annotation = TestClassWithoutAttributes.class.getAnnotation(CdslAttributes.class);
        Assert.assertNull(annotation);
        // This should throw NullPointerException when trying to access annotation properties
        annotation.names();
    }

    @CdslAttributes(names = {"testName1", "testName2"})
    private static class TestClassWithAttributes {}

    @CdslAttributes(names = {})
    private static class TestClassWithEmptyNames {}

    private static class TestClassWithoutAttributes {}
}