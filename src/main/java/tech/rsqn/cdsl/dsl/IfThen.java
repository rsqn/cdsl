package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@CdslDef("if-this")
@Component
public class IfThen extends DslSupport {

    @Override
    public CdslOutputEvent execSupport (CdslContext ctx, CdslInputEvent input) throws CdslException {
        return null;
    }
}
