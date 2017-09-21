package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@CdslDef("static-test-support")
@CdslModel(MapModel.class)
public class StaticDslTestSupport extends DslSupport<MapModel, Serializable> {
    public static Dsl staticDsl;

    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, MapModel model, CdslInputEvent input) throws CdslException {
        if (staticDsl != null) {
            try {
                return staticDsl.execute(runtime, ctx, model, input);
            } catch (Exception e) {
                throw new CdslException(e);
            }
        }
        return null;
    }
}
