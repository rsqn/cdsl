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
 * Supports condition expression (literal or context syntax) or legacy var/val.
 */
@CdslDef("if")
@CdslModel(IfModel.class)
@Component
public class If extends AbstractNestedDsl<IfModel, Serializable> {

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, IfModel model, CdslInputEvent input) throws CdslException {
        if (model == null) {
            return null;
        }
        boolean result = evaluateCondition(ctx, model);
        if (!result) {
            return null;
        }
        return runNestedElements(runtime, ctx, input);
    }

    private boolean evaluateCondition(CdslContext ctx, IfModel model) {
        String cond = model.getCondition();
        if (StringUtils.isEmpty(cond)) {
            return false;
        }
        return evaluateConditionExpression(ctx, cond.trim());
    }

    /**
     * Evaluates a condition expression:
     * - Literal: "true" / "false"
     * - Var exists: "varName" (true when non-null, non-empty)
     * - Equals: "varName=value"
     * - Not equals: "varName!=value"
     */
    private boolean evaluateConditionExpression(CdslContext ctx, String expr) {
        if ("true".equalsIgnoreCase(expr)) {
            return true;
        }
        if ("false".equalsIgnoreCase(expr)) {
            return false;
        }
        int notEq = expr.indexOf("!=");
        if (notEq > 0) {
            String varName = expr.substring(0, notEq).trim();
            String expected = expr.substring(notEq + 2).trim();
            String actual = ctx.getVar(varName);
            return !expected.equals(actual);
        }
        int eq = expr.indexOf('=');
        if (eq > 0) {
            String varName = expr.substring(0, eq).trim();
            String expected = expr.substring(eq + 1).trim();
            String actual = ctx.getVar(varName);
            return expected.equals(actual);
        }
        String actual = ctx.getVar(expr);
        return StringUtils.isNotEmpty(actual);
    }
}
