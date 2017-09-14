package tech.rsqn.cdsl.dsl;

import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.dsl.guards.GuardCondition;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.ArrayList;
import java.util.List;

@CdslDef("dslWithComplexModel")
@CdslModel(ComplexModel.class)
@Component
public class DslWithComplexModel extends DslSupport<ComplexModel> {
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

    public DslWithComplexModel withGuardCondition(GuardCondition g) {
        this.guardConditions.add(g);
        return this;
    }

    public CdslOutputEvent execSupport(CdslContext ctx, ComplexModel model, CdslInputEvent input) throws CdslException {
        return null;
    }
}
