package tech.rsqn.cdsl.dsl;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
import tech.rsqn.cdsl.context.CdslContextAuditorUnitTestSupport;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@Test
@ContextConfiguration(locations = {"classpath:/spring/test-registry-ctx.xml"})
public class DslInterfaceTest {
    /*
    /*
    - pass in context
    - act on context
    - route
    - provide outputs
    - pause
    - resume / white list
    - raise errors
    - auto bind

    - obtain secondary lock
     */

    private DslTestSupport supp;
    private CdslContext ctx;
    private CdslContextAuditor auditor;
    private CdslRuntime runtime;

    @BeforeMethod
    public void setUp() throws Exception {
        supp = new DslTestSupport();
        ctx = new CdslContext();
        runtime = new CdslRuntime();
        auditor = new CdslContextAuditorUnitTestSupport();

        runtime.setAuditor(auditor);
        supp.setAuditor(auditor);
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldRaiseError() throws Exception {
        supp.withGenericCdslException();
        supp.execute(runtime, ctx, null, new CdslInputEvent());
    }

    @Test
    public void shouldPassRoutingInformation() throws Exception {
        supp.withDsl((runtime, ctx, model, input) -> {
            return new CdslOutputEvent().withRoute("stageTwo");
        });

        CdslOutputEvent out = supp.execute(runtime, ctx, null, new CdslInputEvent());
        Assert.assertEquals(out.getNextRoute(), "stageTwo");
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Route);
    }

    @Test
    public void shouldModifyContext() throws Exception {
        Assert.assertNull(ctx.fetchVar("myVar"));

        supp.withDsl((runtime, ctx, model, input) -> {
            ctx.putVar("myVar", "wasSet");
            return null;
        });

        supp.execute(runtime, ctx, null, new CdslInputEvent());
        Assert.assertEquals(ctx.fetchVar("myVar"), "wasSet");
    }


    @Test
    public void shouldPauseExecution() throws Exception {

        supp.withDsl((runtime, ctx, model, input) -> {
            return new CdslOutputEvent().awaitInputAt("awaitHere");
        });

        CdslOutputEvent out = supp.execute(runtime, ctx, null, new CdslInputEvent());
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Await);
        Assert.assertEquals(out.getNextRoute(), "awaitHere");
    }

}