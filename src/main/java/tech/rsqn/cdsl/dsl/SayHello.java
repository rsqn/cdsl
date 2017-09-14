package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@CdslDef("sayHello")
@CdslModel(SayHelloModel.class)
@Component
public class SayHello extends DslSupport<SayHelloModel> {

    @Override
    public CdslOutputEvent execSupport(CdslContext ctx, SayHelloModel config, CdslInputEvent input) throws CdslException {
        System.out.println("Hello " + config.getName());
        return null;
    }
}
