package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;

@FunctionalInterface
public interface Dsl<T,MT extends Serializable> {

    /**
     * If a DSL returns an Output, execution will stop at that point and an action will be taken based on the output.
     *
     * If you wish to return a value, put it in the context
     * @param runtime
     * @param ctx
     * @param model
     * @param input
     * @return
     * @throws CdslException
     */
    CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, T model, CdslInputEvent<MT> input) throws CdslException;
}
