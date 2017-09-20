package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@CdslDef("test-support")
public class DslTestSupport extends DslSupport<Object,Serializable> {
    private List<String> behaviours = new ArrayList<>();
    private Dsl c;

    public DslTestSupport withGenericCdslException() {
        behaviours.add("raise-exception");
        return this;
    }

    public DslTestSupport withDsl(Dsl c) {
        this.c = c;
        return this;
    }

    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, Object model, CdslInputEvent input) throws CdslException {
        if ( c != null ) {
            try {
                return c.execute(runtime, ctx, model, input);
            } catch (Exception e) {
               throw new CdslException(e);
            }
        }

        if ( behaviours.contains("raise-exception")) {
            throw new CdslException("raise-exception");
        }

        return null;
    }
}
