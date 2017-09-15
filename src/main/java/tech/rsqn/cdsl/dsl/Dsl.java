package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@FunctionalInterface
public interface Dsl<T> {
    CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, T cfg, CdslInputEvent input) throws CdslException;
}
