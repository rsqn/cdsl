package tech.rsqn.cdsl;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-integration-ctx.xml"})
public class CdslRunnerTest {

    // obtain lock
    // get context
    // do stuff
    // save context
    // release lock
    // route output?? - no?
    // honour guard conditions
    // handle errors
    //

    @Test
    public void shouldRunHelloWorldAndEndRoute() throws Exception {
        Assert.assertTrue(false);
    }

    @Test
    public void shouldObtainLock() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldLoadContextAfterLock() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldReleaseLockAfterExecution() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldSaveContextAfterExecution() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldRouteToErrorHandlersOnException() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldRouteThroughVariableAppendersAndEndRoute() throws Exception {
        Assert.assertTrue(false);


    }

    @Test
    public void shouldRouteThroughMultipleStepsAndAwait() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldContinueFromAwaitBlock() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldNotContinueIfGuardConditionNotMet() throws Exception {
        Assert.assertTrue(false);

    }
}
