package tech.rsqn.cdsl.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.concurrency.Lock;
import tech.rsqn.cdsl.concurrency.LockProviderUnitTestSupport;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditorUnitTestSupport;
import tech.rsqn.cdsl.context.CdslContextRepositoryUnitTestSupport;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.dsl.Dsl;
import tech.rsqn.cdsl.dsl.DslTestSupport;
import tech.rsqn.cdsl.dsl.MapModel;
import tech.rsqn.cdsl.dsl.StaticDslTestSupport;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.registry.DslInitialisationHelper;
import tech.rsqn.cdsl.registry.FlowRegistry;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-integration-ctx.xml"})
public class FlowExecutorTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private FlowRegistry flowRegistry;

    @Autowired
    private DslInitialisationHelper dslHelper;

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
        contextRepository.getContexts().clear();
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
        Assert.assertTrue(testAuditor.didLogEvent("execute/shouldRunHelloWorldAndEndRoute.init.routeTo"));
        // assert that the auditor passed through ABC
    }

    @Test
    public void shouldRunHelloWorldAndEndRoute() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunHelloWorldAndEndRoute");
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        Assert.assertTrue(testAuditor.didLogEvent("execute/shouldRunHelloWorldAndEndRoute.init.sayHello"));
        Assert.assertTrue(testAuditor.didLogEvent("execute/shouldRunHelloWorldAndEndRoute.end.endRoute"));
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

        Assert.assertTrue(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.a"));
        Assert.assertTrue(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.b"));
        Assert.assertFalse(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));

        System.out.println(output);

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertEquals(context.getCurrentStep(), "c");
        Assert.assertEquals(context.getState(), CdslContext.State.Await);
    }

    @Test()
    public void shouldContinueFromAwaitBlock() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));

        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertFalse(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        executor.execute(flow, new CdslInputEvent().with("test", "message").andContextId(output.getContextId()));

        context = contextRepository.getContext(output.getContextId());

        Assert.assertTrue(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        Assert.assertEquals(context.getCurrentStep(), "end");
        Assert.assertEquals(context.getState(), CdslContext.State.End);

    }

    @Test
    public void shouldRejectIfWhiteListRejectsAndNoRouteSet() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "messageOne"));


        Assert.assertFalse(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));

        executor.execute(flow, new CdslInputEvent().with("test", "messageFail").andContextId(output.getContextId()));
        CdslContext context = contextRepository.getContext(output.getContextId());

        Assert.assertTrue(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
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
        Flow flow = flowRegistry.getFlow("shouldRunInjected");
        MapModel inputModel = new MapModel();

        final AtomicInteger ctr = new AtomicInteger(0);

        dslHelper.inject("injectedOne", (runtime, ctx, model, input)-> {
            runtime.postCommit(()-> {
                ctr.incrementAndGet();
            });
            return null;
        });

        Assert.assertEquals(ctr.get(),0);
        executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
        Assert.assertEquals(ctr.get(),1);
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldRaiseErrorOnIncorrectTransactionId() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunInjected");
        MapModel inputModel = new MapModel();

        dslHelper.inject("injectedOne", (runtime, ctx, model, input)-> {
            runtime.setTransactionId("123");
            return null;
        });

        executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));
    }


    @Test
    public void shouldAuditVariableChanges() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunInjected");
        MapModel inputModel = new MapModel();

        dslHelper.inject("injectedOne", (runtime, ctx, model, input)-> {
            ctx.putVar("testChange","testValue");
            return null;
        });

        executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));

        Assert.assertTrue(testAuditor.didLogEvent("setVar/testChange"));
    }

    @Test
    public void shouldAuditStepChanges() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRunInjected");
        MapModel inputModel = new MapModel();

        dslHelper.inject("injectedOne", (runtime, ctx, model, input)-> {
            return null;
        });

        executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));

        Assert.assertTrue(testAuditor.didLogEvent("execute/shouldRunInjected.end"));

    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldNotRunIfStateIsEnd() throws Exception {

        Flow flow = flowRegistry.getFlow("shouldRunInjected");
        MapModel inputModel = new MapModel();

        dslHelper.inject("injectedOne", (runtime, ctx, model, input)-> {
            return null;
        });

        CdslOutputEvent out = executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));

        Assert.assertTrue(testAuditor.didLogEvent("transition/shouldRunInjected.end"));

        executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel).andContextId(out.getContextId()));


    }

    @Test
    public void shouldRetryWithinBoundariesOnLockRejection() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);

        AtomicReference<String> resource = new AtomicReference<>();
        lockProvider.onLockCallback((Lock lock)-> {
            resource.set(lock.getGrantedResource());
        });

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));
        Lock lockA = lockProvider.obtain("test", resource.get(),1000,1,1000);
        Assert.assertNotNull(lockA);

        Assert.assertFalse(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        executor.execute(flow, new CdslInputEvent().with("test", "message").andContextId(output.getContextId()));
        Assert.assertTrue(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
    }


    @Test
    public void shouldRejectIfLockRetriesExceeded() throws Exception {
        Flow flow = flowRegistry.getFlow("shouldRouteThroughAThenBThenAwaitAtC");
        Assert.assertEquals(contextRepository.getContexts().size(), 0);

        AtomicReference<String> resource = new AtomicReference<>();
        lockProvider.onLockCallback((Lock lock)-> {
            resource.set(lock.getGrantedResource());
        });

        CdslOutputEvent output = executor.execute(flow, new CdslInputEvent().with("test", "message"));
        Lock lockA = lockProvider.obtain("test", resource.get(),10000,1,1000);
        Assert.assertNotNull(lockA);

        Assert.assertFalse(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
        executor.execute(flow, new CdslInputEvent().with("test", "message").andContextId(output.getContextId()));

        Assert.assertFalse(testAuditor.didLogEvent("transition/shouldRouteThroughAThenBThenAwaitAtC.c"));
    }


    @Test(expectedExceptions = CdslException.class)
    //todo- perform this test to a DslModelBuilder only test
    public void shouldPopulateMapModel() throws Exception {
        Flow flow = flowRegistry.getFlow("mapModelFlow");

        Assert.assertEquals(contextRepository.getContexts().size(), 0);
        AtomicReference<String> attrResource = new AtomicReference<>();
        AtomicReference<String> elemResource = new AtomicReference<>();

        StaticDslTestSupport.staticDsl = new Dsl<MapModel,Serializable>() {
            @Override
            public CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, MapModel model, CdslInputEvent<Serializable> input) throws CdslException {

                attrResource.set(model.getMap().get("attrOne"));
                elemResource.set(model.getMap().get("elemOne"));

                return null;
            }
        };

        executor.execute(flow, new CdslInputEvent().with("test", "message"));

        Assert.assertEquals("attrOneValue",attrResource.get());
        Assert.assertEquals("elemOneValue",elemResource.get());
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldThrowErrorWhenRoutingToInvalidTarget() throws Exception {

        Flow flow = flowRegistry.getFlow("shouldRunInjected");
        MapModel inputModel = new MapModel();

        dslHelper.inject("injectedOne", (runtime, ctx, model, input)-> {
            return new CdslOutputEvent().withRoute("wrong-turn");
        });

        executor.execute(flow, new CdslInputEvent().with("test", "message").andModel(inputModel));

    }

}
