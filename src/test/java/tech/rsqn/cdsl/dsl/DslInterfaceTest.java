package tech.rsqn.cdsl.dsl;

import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
import tech.rsqn.cdsl.dsl.guards.EventNameGuardCondition;
import tech.rsqn.cdsl.dsl.guards.SourceGuardCondition;
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

    @BeforeMethod
    public void setUp() throws Exception {
        supp = new DslTestSupport();
        ctx = new CdslContext();

        auditor = new CdslContextAuditor();

        supp.setAuditor(auditor);
        ctx.setAuditor(auditor);
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldRaiseError() throws Exception {
        supp.withGenericCdslException();
        supp.execute(ctx, null, new CdslInputEvent());
    }

    @Test
    public void shouldPassRoutingInformation() throws Exception {
        supp.withDsl((ctx, model, input) -> {
            return new CdslOutputEvent().withRoute("stageTwo");
        });

        CdslOutputEvent out = supp.execute(ctx, null, new CdslInputEvent());
        Assert.assertEquals(out.getNextRoute(), "stageTwo");
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Route);
    }

    @Test
    public void shouldModifyContext() throws Exception {
        Assert.assertNull(ctx.getVar("myVar"));

        supp.withDsl((ctx, model, input) -> {
            ctx.setVar("myVar", "wasSet");
            return null;
        });

        supp.execute(ctx, null, new CdslInputEvent());
        Assert.assertEquals(ctx.getVar("myVar"), "wasSet");
    }


    @Test
    public void shouldPauseExecution() throws Exception {

        supp.withDsl((ctx, model, input) -> {
            return new CdslOutputEvent().awaitInputAt("awaitHere");
        });

        CdslOutputEvent out = supp.execute(ctx, null, new CdslInputEvent());
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Await);
        Assert.assertEquals(out.getNextRoute(), "awaitHere");
    }

    @Test
    public void shouldRejectInvalidInputEventGuardCondition() throws Exception {

        EventNameGuardCondition guard = new EventNameGuardCondition();
        guard.setAccept("one");
        supp.withGuardCondition(guard);

        CdslOutputEvent out = supp.execute(ctx, null, new CdslInputEvent());
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Reject);
    }

    @Test
    public void shouldAcceptValidInputEventGuardCondition() throws Exception {
        EventNameGuardCondition guard = new EventNameGuardCondition();
        guard.setAccept("one");
        supp.withGuardCondition(guard);

        supp.withDsl((ctx, model, input) -> {
            return new CdslOutputEvent().awaitInputAt("awaitHere");
        });

        CdslOutputEvent out = supp.execute(ctx, null, new CdslInputEvent().with("any", "one"));
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Await);
    }

    @Test
    public void shouldRejectInvalidSourceGuardCondition() throws Exception {
        SourceGuardCondition guard = new SourceGuardCondition();
        guard.setAccept("NO");
        supp.withGuardCondition(guard);

        supp.withDsl((ctx, model, input) -> {
            return new CdslOutputEvent().awaitInputAt("awaitHere");
        });

        CdslOutputEvent out = supp.execute(ctx, null, new CdslInputEvent().with("any", "one"));
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Reject);
    }

    @Test
    public void shouldAcceptValidSourceGuardCondition() throws Exception {
        SourceGuardCondition guard = new SourceGuardCondition();
        guard.setAccept("YES");
        supp.withGuardCondition(guard);

        supp.withDsl((ctx, model, input) -> {
            return new CdslOutputEvent().awaitInputAt("awaitHere");
        });

        CdslOutputEvent out = supp.execute(ctx, null, new CdslInputEvent().with("YES", "one"));
        Assert.assertEquals(out.getAction(), CdslOutputEvent.Action.Await);
    }

}