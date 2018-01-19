package tech.rsqn.cdsl.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;

public abstract class DslSupport<T,MT extends Serializable> implements Dsl<T,MT> {
    protected static final Logger logger = LoggerFactory.getLogger(DslSupport.class);
    protected transient CdslContextAuditor auditor;

    public void setAuditor(CdslContextAuditor auditor) {
        this.auditor = auditor;
    }


    public  final CdslOutputEvent execute(CdslRuntime runtime, CdslContext ctx, T model, CdslInputEvent<MT> input) throws CdslException {
        ctx.setRuntime(runtime);
        return execSupport(runtime,ctx, model, input);
    }

    public abstract CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, T model, CdslInputEvent<MT> input) throws CdslException;


}
