package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@CdslDef("test-support")
public class DslTestSupport extends DslSupport {

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


    @Override
    public CdslOutputEvent execute(CdslContext ctx, CdslInputEvent input) throws CdslException {
        if ( c != null ) {
            try {
                return c.execute(ctx,input);
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
