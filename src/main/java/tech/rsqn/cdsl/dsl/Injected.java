package tech.rsqn.cdsl.dsl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.registry.DslInitialisationHelper;

import java.io.Serializable;

@CdslDef("injected")
@CdslModel(InjectedModel.class)
@Component
public class Injected extends DslSupport<InjectedModel,Serializable> implements ValidatingDsl<InjectedModel> {

    @Autowired
    DslInitialisationHelper dslHelper;

    @Override
    public void validate(InjectedModel cfg) throws CdslException {
//        CdslContext.State.valueOf(cfg.getVal()).toString();
    }

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, InjectedModel model, CdslInputEvent input) throws CdslException {

        Dsl dsl = dslHelper.resolveInjected(model.getName());

        if ( dsl != null ) {
            return dsl.execute(runtime,ctx,model,input);
        }

        throw new CdslException("Unable to resolve injected DSL " + model.getName());
    }
}
