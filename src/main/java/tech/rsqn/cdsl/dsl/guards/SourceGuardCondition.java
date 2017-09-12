package tech.rsqn.cdsl.dsl.guards;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.model.CdslInputEvent;

public class SourceGuardCondition implements GuardCondition {

    private String accept;

    public void setAccept(String accept) {
        this.accept = accept;
    }

    @Override
    public boolean accept(CdslContext ctx, CdslInputEvent input) {
        if (accept.equals(input.getSource())) {
            return true;
        }
        return false;
    }
}
