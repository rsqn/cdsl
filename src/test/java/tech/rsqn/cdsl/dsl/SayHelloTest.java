package tech.rsqn.cdsl.dsl;

import org.testng.Assert;
import org.testng.annotations.Test;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@Test
public class SayHelloTest {

    @Test
    public void shouldSayHelloWithName() throws CdslException {
        SayHello sayHello = new SayHello();
        CdslRuntime runtime = new CdslRuntime();
        CdslContext context = new CdslContext();
        SayHelloModel model = new SayHelloModel();
        model.setName("TestUser");

        CdslOutputEvent output = sayHello.execSupport(runtime, context, model, new CdslInputEvent());

        Assert.assertNull(output); // SayHello returns null
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldThrowExceptionWhenModelIsNull() throws CdslException {
        SayHello sayHello = new SayHello();
        CdslRuntime runtime = new CdslRuntime();
        CdslContext context = new CdslContext();
        sayHello.execSupport(runtime, context, null, new CdslInputEvent());
    }

    @Test(expectedExceptions = CdslException.class)
    public void shouldThrowExceptionWhenNameIsNull() throws CdslException {
        SayHello sayHello = new SayHello();
        CdslRuntime runtime = new CdslRuntime();
        CdslContext context = new CdslContext();
        SayHelloModel model = new SayHelloModel();
        sayHello.execSupport(runtime, context, model, new CdslInputEvent());
    }
}