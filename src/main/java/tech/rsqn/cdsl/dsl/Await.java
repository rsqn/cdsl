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

@CdslDef("await")
@CdslModel(AwaitModel.class)
@Component
public class Await extends DslSupport<AwaitModel,Serializable> {
    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, AwaitModel model, CdslInputEvent input) throws CdslException {
        if (model == null) {
            throw new CdslException("Await model cannot be null");
        }
        if (model.getAt() == null) {
            throw new CdslException("Await 'at' property cannot be null");
        }
        return new CdslOutputEvent().awaitInputAt(model.getAt());
    }
}
