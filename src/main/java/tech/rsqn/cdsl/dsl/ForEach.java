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
 * Container DSL: runs nested elements once for each item in a list.
 * List is read from a context variable as a string (e.g. comma-separated).
 * Each iteration sets the current item into another context variable, then runs the body.
 * If the body returns a Route/Await/End, that result is returned and the loop stops.
 */
@CdslDef("foreach")
@CdslModel(ForEachModel.class)
@Component
public class ForEach extends AbstractNestedDsl<ForEachModel, Serializable> {

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, ForEachModel model, CdslInputEvent input) throws CdslException {
        if (model == null || StringUtils.isEmpty(model.getListVar()) || StringUtils.isEmpty(model.getItemVar())) {
            return null;
        }
        String listStr = ctx.getVar(model.getListVar());
        if (StringUtils.isEmpty(listStr)) {
            return null;
        }
        String sep = StringUtils.isNotEmpty(model.getSeparator()) ? model.getSeparator() : ",";
        String[] items = listStr.split(sep);
        for (String item : items) {
            String trimmed = item != null ? item.trim() : "";
            ctx.putVar(model.getItemVar(), trimmed);
            CdslOutputEvent output = runNestedElements(runtime, ctx, input);
            if (output != null) {
                return output;
            }
        }
        return null;
    }
}
