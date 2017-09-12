package tech.rsqn.cdsl.dsl.guards;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.model.CdslInputEvent;

public interface GuardCondition {
    boolean accept(CdslContext ctx, CdslInputEvent input);
}
