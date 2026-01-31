package tech.rsqn.cdsl.annotations;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CdslModelTest {

    @Test
    public void shouldRetrieveClassFromAnnotation() {
        CdslModel annotation = TestClassWithModel.class.getAnnotation(CdslModel.class);
        Assert.assertNotNull(annotation);
        Assert.assertEquals(annotation.value(), String.class);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowExceptionWhenAnnotationMissing() {
        CdslModel annotation = TestClassWithoutModel.class.getAnnotation(CdslModel.class);
        Assert.assertNull(annotation);
        // This should throw NullPointerException when trying to access annotation properties
        annotation.value();
    }

    @CdslModel(String.class)
    private static class TestClassWithModel {}

    private static class TestClassWithoutModel {}
}