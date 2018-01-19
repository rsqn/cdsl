package tech.rsqn.cdsl.registry;

import org.testng.Assert;
import org.testng.annotations.Test;

public class RegistryValidatorTest {

    @Test(enabled=false)
    public void shouldFailBecauseIHaventMadeTests() throws Exception {
        Assert.assertTrue(false);
    }


    @Test(enabled=false)
    public void shouldNotAllowDuplicateStepNames() throws Exception {
        Assert.assertTrue(false);
    }
}
