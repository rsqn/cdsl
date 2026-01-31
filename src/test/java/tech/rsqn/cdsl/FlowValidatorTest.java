package tech.rsqn.cdsl;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class FlowValidatorTest {

    @Test
    public void shouldCreateInstance() {
        FlowValidator validator = new FlowValidator();
        Assert.assertNotNull(validator);
    }

    @Test
    public void shouldBeInstantiable() {
        // Test that the class can be instantiated without errors
        FlowValidator validator1 = new FlowValidator();
        FlowValidator validator2 = new FlowValidator();
        
        Assert.assertNotNull(validator1);
        Assert.assertNotNull(validator2);
        Assert.assertNotSame(validator1, validator2);
    }

    @Test
    public void shouldHaveCorrectClassName() {
        FlowValidator validator = new FlowValidator();
        Assert.assertEquals(validator.getClass().getSimpleName(), "FlowValidator");
        Assert.assertEquals(validator.getClass().getName(), "tech.rsqn.cdsl.FlowValidator");
    }
}