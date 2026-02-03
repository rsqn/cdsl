package tech.rsqn.cdsl.dsl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;

/**
 * Container DSL: runs nested elements only when the condition holds.
 * Condition is based on a context variable: optional "var" and "val".
 * - If both var and val are set: run body when context.getVar(var) equals val.
 * - If only var is set: run body when context.getVar(var) is not null and not empty.
 */
@CdslDef("if")
@CdslModel(IfModel.class)
@Component
public class If extends AbstractNestedDsl<IfModel, Serializable> {

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, IfModel model, CdslInputEvent input) throws CdslException {
        if (model == null || StringUtils.isEmpty(model.getVar())) {
            return null;
        }
        String actual = ctx.getVar(model.getVar());
        boolean condition;
        if (StringUtils.isNotEmpty(model.getVal())) {
            condition = model.getVal().equals(actual);
        } else {
            condition = StringUtils.isNotEmpty(actual);
        }
        if (!condition) {
            return null;
        }
        return runNestedElements(runtime, ctx, input);
    }
}
