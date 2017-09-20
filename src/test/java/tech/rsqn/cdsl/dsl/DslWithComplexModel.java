package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@CdslDef("dslWithComplexModel")
@CdslModel(ComplexModel.class)
@Component
public class DslWithComplexModel extends DslSupport<ComplexModel,Serializable> {
    private List<String> behaviours = new ArrayList<>();
    private Dsl c;

    public DslWithComplexModel withGenericCdslException() {
        behaviours.add("raise-exception");
        return this;
    }

    public DslWithComplexModel withDsl(Dsl c) {
        this.c = c;
        return this;
    }

    public CdslOutputEvent execSupport(CdslRuntime runtime ,CdslContext ctx, ComplexModel model, CdslInputEvent input) throws CdslException {
        return null;
    }
}
