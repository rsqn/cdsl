package tech.rsqn.cdsl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.concurrency.LockProviderUnitTestSupport;
import tech.rsqn.cdsl.context.*;
import tech.rsqn.cdsl.execution.FlowExecutor;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.model.Flow;
import tech.rsqn.cdsl.registry.FlowRegistry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-integration-ctx.xml"})
public class FlowExecutorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private FlowRegistry flowRegistry;

    @Autowired
    private FlowExecutor executor;

    @Autowired
    private CdslContextRepositoryUnitTestSupport contextRepository;

    @Autowired
    private CdslContextAuditorUnitTestSupport testAuditor;

    @Autowired
    private LockProviderUnitTestSupport lockProvider;


    @BeforeMethod
    public void setUp() throws Exception {
//        testAuditor = new CdslContextAuditorUnitTestSupport();
    }

    @Test
    public void shouldExecuteInitStepIfNoStatePresent() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));

        Assert.assertNotNull(output);
        Assert.assertNotNull(output.getContextId());

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertNotNull(context);
        Assert.assertEquals(context.getCurrentStep(),"end");

        System.out.println(testAuditor);
        Assert.assertTrue(testAuditor.didExecute("shouldRunHelloWorldAndEndRoute.init.routeTo"));
        // assert that the auditor passed through ABC
    }

    @Test
    public void shouldRunHelloWorldAndEndRoute() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));

        Assert.assertTrue(testAuditor.didExecute("shouldRunHelloWorldAndEndRoute.init.sayHello"));
        Assert.assertTrue(testAuditor.didExecute("shouldRunHelloWorldAndEndRoute.end.endRoute"));
    }


    @Test
    public void shouldSetOutputState() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));
        CdslContext context = contextRepository.getContext(output.getContextId());
        Assert.assertEquals(context.getState(), CdslContext.State.End);
        Assert.assertEquals(output.getContextState(), CdslContext.State.End);
    }



    @Test
    public void shouldObtainLockAndThenUnlock() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");

        final AtomicInteger locks = new AtomicInteger(0);
        final AtomicInteger unlocks = new AtomicInteger(0);

        lockProvider.onLockCallback((lock) -> {
            locks.incrementAndGet();
        });

        lockProvider.onUnLockCallback((lock) -> {
            unlocks.incrementAndGet();
        });

        Assert.assertEquals(locks.get(),0);

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));

        Assert.assertEquals(locks.get(),1);
        Assert.assertEquals(unlocks.get(),1);

    }

    @Test
    public void shouldSaveContextAfterExecution() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");

        Assert.assertEquals(contextRepository.getContexts().size(),0);

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));
        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertEquals(context.getVar("myVar"),"myVal");
    }

    @Test
    public void shouldRouteToErrorHandlersOnException() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        Assert.assertEquals(contextRepository.getContexts().size(),0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test","message"));
        CdslContext context = contextRepository.getContext(output.getContextId());
        Assert.assertEquals(context.getVar("errorRaised"),"yesItWas");
        Assert.assertEquals(context.getCurrentStep(),"error");
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


    @Test
    public void shouldPopulateMapModel() throws Exception {
        Assert.assertTrue(false);

    }

}
