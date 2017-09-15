package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@CdslDef("routeTo")
@CdslModel(RouteToModel.class)
@Component
public class RouteTo extends DslSupport<RouteToModel> {
    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, RouteToModel cfg, CdslInputEvent input) throws CdslException {
        return new CdslOutputEvent().withRoute(cfg.getTarget());
    }
}
