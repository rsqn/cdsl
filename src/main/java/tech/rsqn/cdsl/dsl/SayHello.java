package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;

@CdslDef("sayHello")
@CdslModel(SayHelloModel.class)
@Component
public class SayHello extends DslSupport<SayHelloModel,Serializable> {

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, SayHelloModel config, CdslInputEvent input) throws CdslException {
        if (config == null) {
            throw new CdslException("SayHello model cannot be null");
        }
        if (config.getName() == null) {
            throw new CdslException("SayHello 'name' property cannot be null");
        }
        System.out.println("Hello " + config.getName());
        return null;
    }
}
