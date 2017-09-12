package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

public abstract class DslSupport implements Dsl{
    /*
    - pass in context
    - act on context
    - route
    - provide outputs
    - pause
    - resume / white list
    - raise errors
    - auto bind
     */


    public abstract CdslOutputEvent execute(CdslContext ctx, CdslInputEvent input) throws CdslException;
}
