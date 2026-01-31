package tech.rsqn.cdsl.model;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class CdslInputEventTest {

    @Test
    public void shouldCreateInstanceWithNullValues() {
        CdslInputEvent<String> event = new CdslInputEvent<>();
        Assert.assertNull(event.getContextId());
        Assert.assertNull(event.getName());
        Assert.assertNull(event.getSource());
        Assert.assertNull(event.getRequestedStep());
        Assert.assertNull(event.getModel());
    }

    @Test
    public void shouldSetAndGetValues() {
        CdslInputEvent<String> event = new CdslInputEvent<>();
        event.setContextId("testContextId");
        event.setName("testName");
        event.setSource("testSource");
        event.setRequestedStep("testStep");
        event.setModel("testModel");

        Assert.assertEquals(event.getContextId(), "testContextId");
        Assert.assertEquals(event.getName(), "testName");
        Assert.assertEquals(event.getSource(), "testSource");
        Assert.assertEquals(event.getRequestedStep(), "testStep");
        Assert.assertEquals(event.getModel(), "testModel");
    }

    @Test
    public void shouldUseWithMethod() {
        CdslInputEvent<String> event = new CdslInputEvent<>().with("testSource", "testName");
        Assert.assertEquals(event.getSource(), "testSource");
        Assert.assertEquals(event.getName(), "testName");
        Assert.assertNull(event.getContextId());
        Assert.assertNull(event.getRequestedStep());
        Assert.assertNull(event.getModel());
    }

    @Test
    public void shouldUseAndContextIdMethod() {
        CdslInputEvent<String> event = new CdslInputEvent<>().andContextId("testContextId");
        Assert.assertEquals(event.getContextId(), "testContextId");
        Assert.assertNull(event.getName());
        Assert.assertNull(event.getSource());
        Assert.assertNull(event.getRequestedStep());
        Assert.assertNull(event.getModel());
    }

    @Test
    public void shouldUseAndStepMethod() {
        CdslInputEvent<String> event = new CdslInputEvent<>().andStep("testStep");
        Assert.assertEquals(event.getRequestedStep(), "testStep");
        Assert.assertNull(event.getContextId());
        Assert.assertNull(event.getName());
        Assert.assertNull(event.getSource());
        Assert.assertNull(event.getModel());
    }

    @Test
    public void shouldUseAndModelMethod() {
        CdslInputEvent<String> event = new CdslInputEvent<>().andModel("testModel");
        Assert.assertEquals(event.getModel(), "testModel");
        Assert.assertNull(event.getContextId());
        Assert.assertNull(event.getName());
        Assert.assertNull(event.getSource());
        Assert.assertNull(event.getRequestedStep());
    }

    @Test
    public void shouldHaveProperToString() {
        CdslInputEvent<String> event = new CdslInputEvent<>().with("testSource", "testName");
        String toString = event.toString();
        Assert.assertNotNull(toString);
        Assert.assertTrue(toString.contains("testSource"));
        Assert.assertTrue(toString.contains("testName"));
    }
}