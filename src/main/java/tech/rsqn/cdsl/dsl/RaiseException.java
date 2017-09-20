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

@CdslDef("raiseException")
@CdslModel(MapModel.class)
@Component
public class RaiseException extends DslSupport<MapModel,Serializable> {
    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime,  CdslContext ctx, MapModel model, CdslInputEvent input) throws CdslException {
        throw new CdslException("RaiseException");
    }
}
