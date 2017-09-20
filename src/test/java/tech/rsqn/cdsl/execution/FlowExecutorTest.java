package tech.rsqn.cdsl.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.concurrency.LockProviderUnitTestSupport;
import tech.rsqn.cdsl.context.*;
import tech.rsqn.cdsl.dsl.DslSupport;
import tech.rsqn.cdsl.dsl.MapModel;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.registry.FlowRegistry;

import java.util.concurrent.atomic.AtomicInteger;

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
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        Assert.assertNotNull(output);
        Assert.assertNotNull(output.getContextId());

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertNotNull(context);
        Assert.assertEquals(context.getCurrentStep(), "end");

        System.out.println(testAuditor);
        Assert.assertTrue(testAuditor.didExecute("shouldRunHelloWorldAndEndRoute.init.routeTo"));
        // assert that the auditor passed through ABC
    }

    @Test
    public void shouldRunHelloWorldAndEndRoute() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        Assert.assertTrue(testAuditor.didExecute("shouldRunHelloWorldAndEndRoute.init.sayHello"));
        Assert.assertTrue(testAuditor.didExecute("shouldRunHelloWorldAndEndRoute.end.endRoute"));
    }


    @Test
    public void shouldSetOutputState() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));
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

        Assert.assertEquals(locks.get(), 0);

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        Assert.assertEquals(locks.get(), 1);
        Assert.assertEquals(unlocks.get(), 1);

    }

    @Test
    public void shouldSaveContextAfterExecution() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");

        Assert.assertEquals(contextRepository.getContexts().size(), 0);

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));
        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertEquals(context.fetchVar("myVar"), "myVal");
    }

    @Test
    public void shouldRouteToErrorHandlersOnException() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));
        CdslContext context = contextRepository.getContext(output.getContextId());
        Assert.assertEquals(context.fetchVar("errorRaised"), "yesItWas");
        Assert.assertEquals(context.getCurrentStep(), "error");
    }


    @Test
    public void shouldRouteThroughMultipleStepsAndAwait() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        Assert.assertTrue(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.a"));
        Assert.assertTrue(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.b"));
        Assert.assertFalse(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));

        System.out.println(output);

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertEquals(context.getCurrentStep(), "c");
        Assert.assertEquals(context.getState(), CdslContext.State.Await);
    }

    @Test
    public void shouldContinueFromAwaitBlock() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertFalse(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        executor.execute(flow, new CdslInputEvent().with("test", "message").andContextId(output.getContextId()));

        Assert.assertTrue(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        Assert.assertEquals(context.getCurrentStep(), "end");
        Assert.assertEquals(context.getState(), CdslContext.State.End);

    }

    @Test
    public void shouldRejectIfWhiteListRejectsAndNoRouteSet() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "messageOne"));


        Assert.assertFalse(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));

        executor.execute(flow, new CdslInputEvent().with("test", "messageFail").andContextId(output.getContextId()));
        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertTrue(testAuditor.didTransition("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        Assert.assertEquals(context.getCurrentStep(), "c");
        Assert.assertEquals(context.getState(), CdslContext.State.Await);
    }

    @Test
    public void shoudlRouteIfWhiteListRejectsaAndRouteSet() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldAwaitAtB");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "messageOne"));

        executor.execute(flow, new CdslInputEvent().with("test", "messageFail").andContextId(output.getContextId()));

        CdslContext context = contextRepository.getContext(output.getContextId());
        Assert.assertEquals(context.getCurrentStep(), "end");
        Assert.assertEquals(context.getState(), CdslContext.State.End);
    }

    @Test
    public void shouldPassParametersInModel() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldProcessInputModelA");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);

        CustomInputModel inputModel = new CustomInputModel();
        inputModel.setTheMessage("my message");


        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertEquals(context.fetchVar("myChosenVar"), "egassem ym");
    }

    @Test
    public void shouldBufferOutputMessages() throws Exception {
//        Flow flow = flowRegistry.getFlow("shouldSendBufferedOutputMessages");
//        Assert.assertEquals(contextRepository.getContexts().size(), 0);
//
//        MapModel inputModel = new MapModel();
//        inputModel.put("sendMessage","one");
//
//        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
//
//        CdslContext context = contextRepository.getContext(output.getContextId());
//
//        Assert.assertEquals(context.fetchVar("output"), "end");
//        Assert.assertEquals(context.getState(), CdslContext.State.End);
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

    @Test
    public void shouldThrowErrorWhenRoutingToInvalidId() throws Exception {
        Assert.assertTrue(false);
    }

}
