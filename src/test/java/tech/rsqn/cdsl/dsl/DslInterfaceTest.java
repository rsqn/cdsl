package tech.rsqn.cdsl.dsl;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
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

    @BeforeMethod
    public void setUp() throws Exception {
        supp = new DslTestSupport();
        ctx = new CdslContext();
        ctx.setAuditor(new CdslContextAuditor());
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldRaiseError() throws Exception {
        supp.withGenericCdslException();
        supp.execute(ctx, new CdslInputEvent());
    }

    @Test
    public void shouldPassRoutingInformation() throws Exception {
        supp.withDsl((ctx, input) -> {
            return new CdslOutputEvent().withRoute("stageTwo");
        });

        CdslOutputEvent out = supp.execute(ctx, new CdslInputEvent());
        Assert.assertEquals(out.getNextRoute(), "stageTwo");
        Assert.assertEquals(out.getAction(), "route");
    }

    @Test
    public void shouldModifyContext() throws Exception {
        Assert.assertNull(ctx.getVar("myVar"));

        supp.withDsl((ctx, input) -> {
            ctx.setVar("myVar", "wasSet");
            return null;
        });

        supp.execute(ctx, new CdslInputEvent());
        Assert.assertEquals(ctx.getVar("myVar"), "wasSet");
    }


    @Test
    public void shouldPauseExecution() throws Exception {

        supp.withDsl((ctx, input) -> {
            return new CdslOutputEvent().awaitInputAt("awaitHere");
        });


        CdslOutputEvent out = supp.execute(ctx, new CdslInputEvent());
        Assert.assertEquals(out.getAction(), "await");
        Assert.assertEquals(out.getNextRoute(), "awaitHere");
    }

    @Test
    public void shouldRejectInvalidGuardCondition() throws Exception {
        CdslOutputEvent out = supp.execute(ctx, new CdslInputEvent());
        Assert.assertEquals(out.getAction(), "getCurrentStepOrNextStepYolo");
    }

    @Test
    public void shouldAcceptValidGuardCondition() throws Exception {
        CdslOutputEvent out = supp.execute(ctx, new CdslInputEvent());
        Assert.assertEquals(out.getAction(), "getCurrentStepOrNextStepYolo");
    }

}