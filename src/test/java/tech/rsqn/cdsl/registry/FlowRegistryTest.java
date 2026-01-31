package tech.rsqn.cdsl.registry;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.definitionsource.FlowDefinition;
import tech.rsqn.cdsl.execution.Flow;

@Test
public class FlowRegistryTest {

    @Test
    public void shouldCreateInstanceWithEmptyFlows() {
        FlowRegistry registry = new FlowRegistry();
        Assert.assertNotNull(registry);
        Assert.assertNull(registry.getFlow("nonExistentFlow")); // Should return null
    }

    @Test
    public void shouldSubmitAndGetFlowDefinition() {
        FlowRegistry registry = new FlowRegistry();
        FlowDefinition definition = new FlowDefinition();
        definition.setId("testFlow");

        registry.submitDefinition(definition);
        Flow flow = registry.getFlow("testFlow");

        Assert.assertNotNull(flow);
        Assert.assertEquals(flow.getId(), "testFlow");
    }

    @Test
    public void shouldHandleMultipleFlowDefinitions() {
        FlowRegistry registry = new FlowRegistry();
        FlowDefinition definition1 = new FlowDefinition();
        definition1.setId("testFlow1");

        FlowDefinition definition2 = new FlowDefinition();
        definition2.setId("testFlow2");

        registry.submitDefinition(definition1);
        registry.submitDefinition(definition2);

        Assert.assertNotNull(registry.getFlow("testFlow1"));
        Assert.assertNotNull(registry.getFlow("testFlow2"));
    }

    @Test
    public void shouldReturnNullForNonExistentFlow() {
        FlowRegistry registry = new FlowRegistry();
        Flow flow = registry.getFlow("nonExistentFlow");
        Assert.assertNull(flow);
    }
}