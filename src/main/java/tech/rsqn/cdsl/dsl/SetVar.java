package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@CdslDef("setVar")
@CdslModel(SetVarModel.class)
@Component
public class SetVar extends DslSupport<SetVarModel>  implements ValidatingDsl<SetVarModel> {

    @Override
    public void validate(SetVarModel cfg) throws CdslException {
//        CdslContext.State.valueOf(cfg.getVal()).toString();
    }

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, SetVarModel model, CdslInputEvent input) throws CdslException {
        ctx.setVar(model.getName(),model.getVal());
        return null;
    }
}
