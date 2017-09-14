package tech.rsqn.cdsl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
import tech.rsqn.cdsl.context.CdslContextRepository;
import tech.rsqn.cdsl.execution.FlowExecutor;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.model.Flow;
import tech.rsqn.cdsl.registry.FlowRegistry;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-integration-ctx.xml"})
public class FlowExecutorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private FlowRegistry flowRegistry;

    @Autowired
    private FlowExecutor executor;

    @Autowired
    private CdslContextRepository contextRepository;

    @Autowired
    private CdslContextAuditor auditor;

    @Test
    public void shouldExecuteInitStepIfNoStatePresent() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));

        Assert.assertNotNull(output);
        Assert.assertNotNull(output.getContextId());

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertNotNull(context);
        Assert.assertEquals(context.getCurrentStep(),"end");

        // assert that the auditor passed through ABC
    }

    @Test
    public void shouldRunHelloWorldAndEndRoute() throws Exception {

//        FlowDefinition flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
//
//        executor.execute(flow, new CdslInputEvent().with("test",));

        Assert.assertTrue(false);
    }


    @Test
    public void shouldSetOutputState() throws Exception {
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

    @Test
    public void shouldPassParametersInModel() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldBufferOutputMessages() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldRaiseErrorOnIncorrectTransactionId() throws Exception {
        Assert.assertTrue(false);

    }


    @Test
    public void shouldAuditVariableChanges() throws Exception {
        Assert.assertTrue(false);
    }

    @Test
    public void shouldAuditStepChanges() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldRetryWithinBoundariesOnLockRejection() throws Exception {
        Assert.assertTrue(false);

    }


    @Test
    public void shouldRejectIfLockRetriesExceeded() throws Exception {
        Assert.assertTrue(false);

    }

    @Test
    public void shouldNotRunIfStateIsEnd() throws Exception {
        Assert.assertTrue(false);

    }


}
