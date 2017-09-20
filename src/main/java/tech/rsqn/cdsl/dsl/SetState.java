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

@CdslDef("setState")
@CdslModel(SetStateModel.class)
@Component
public class SetState  extends DslSupport<SetStateModel,Serializable>  implements ValidatingDsl<SetStateModel> {

    @Override
    public void validate(SetStateModel cfg) throws CdslException {
        CdslContext.State.valueOf(cfg.getVal()).toString();
    }

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, SetStateModel model, CdslInputEvent input) throws CdslException {
        ctx.setState(CdslContext.State.valueOf(model.getVal()));
        return null;
    }
}
