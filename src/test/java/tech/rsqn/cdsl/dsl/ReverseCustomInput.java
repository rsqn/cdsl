package tech.rsqn.cdsl.dsl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.execution.CustomInputModel;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

@CdslDef("reverseCustomInput")
@CdslModel(ReverseCustomInputModel.class)
@Component
public class ReverseCustomInput extends DslSupport<ReverseCustomInputModel, CustomInputModel> {

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, ReverseCustomInputModel model, CdslInputEvent<CustomInputModel> input) throws CdslException {

        CustomInputModel cm = input.getModel();
        System.out.println(cm.getTheMessage());
        String reversed = StringUtils.reverse(cm.getTheMessage());

        ctx.putVar(model.getOutVar(), reversed);
        return null;
    }
}
