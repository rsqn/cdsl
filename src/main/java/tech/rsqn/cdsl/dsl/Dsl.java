package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@FunctionalInterface
public interface Dsl {
    CdslOutputEvent execute(CdslContext ctx, CdslInputEvent input) throws CdslException;
}
