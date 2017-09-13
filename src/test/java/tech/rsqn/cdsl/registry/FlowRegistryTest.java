package tech.rsqn.cdsl.registry;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.model.definition.FlowDefinition;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-flow-registry-ctx.xml"})
public class FlowRegistryTest extends AbstractTestNGSpringContextTests {

    @Autowired
    FlowRegistry flowRegistry;

    @Test
    public void shouldRegisterTestFlows() throws Exception {
        FlowDefinition flow = flowRegistry.getFlow("test-flow-a");
        Assert.assertNotNull(flow);
    }
}
