package tech.rsqn.cdsl.dsl;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@Test
public class AwaitTest {

    @Test
    public void shouldAwaitAtSpecifiedStep() throws CdslException {
        Await await = new Await();
        CdslRuntime runtime = new CdslRuntime();
        CdslContext context = new CdslContext();
        AwaitModel model = new AwaitModel();
        model.setAt("testStep");

        CdslOutputEvent output = await.execSupport(runtime, context, model, new CdslInputEvent());

        Assert.assertNotNull(output);
        Assert.assertEquals(output.getAction(), CdslOutputEvent.Action.Await);
        Assert.assertEquals(output.getNextRoute(), "testStep");
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldThrowExceptionWhenModelIsNull() throws CdslException {
        Await await = new Await();
        CdslRuntime runtime = new CdslRuntime();
        CdslContext context = new CdslContext();
        await.execSupport(runtime, context, null, new CdslInputEvent());
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldThrowExceptionWhenAtIsNull() throws CdslException {
        Await await = new Await();
        CdslRuntime runtime = new CdslRuntime();
        CdslContext context = new CdslContext();
        AwaitModel model = new AwaitModel();
        await.execSupport(runtime, context, model, new CdslInputEvent());
    }
}