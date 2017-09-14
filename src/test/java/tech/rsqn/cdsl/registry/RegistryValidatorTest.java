package tech.rsqn.cdsl.registry;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistryValidatorTest {

    @Test
    public void shouldFailBecauseIHaventMadeTests() throws Exception {
        Assert.assertTrue(false);
    }

    @Test
    public void shouldNotAllowDuplicateStepNames() throws Exception {
        Assert.assertTrue(false);
    }
}
