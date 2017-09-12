package tech.rsqn.cdsl.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
import tech.rsqn.cdsl.dsl.guards.GuardCondition;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class DslSupport implements Dsl {
    protected static final Logger logger = LoggerFactory.getLogger(DslSupport.class);
    protected transient CdslContextAuditor auditor;

    public void setAuditor(CdslContextAuditor auditor) {
        this.auditor = auditor;
    }

    protected List<GuardCondition> guardConditions;

    public DslSupport() {
        guardConditions = new ArrayList<>();
    }

    public GuardCondition filterForGuardCondition(CdslContext ctx, CdslInputEvent input) {
        if (guardConditions.size() > 0) {
            for (GuardCondition guardCondition : guardConditions) {
                if (!guardCondition.accept(ctx, input)) {
                    return guardCondition;
                }
            }
        }
        return null;
    }

    public final CdslOutputEvent execute(CdslContext ctx, CdslInputEvent input) throws CdslException {
        GuardCondition guard = filterForGuardCondition(ctx, input);
        if (guard != null) {
            logger.info("Input " + input + " has been rejected by a guard condition " + guard);
            auditor.reject(ctx, guard.toString());
            return new CdslOutputEvent().reject(guard);
        }

        return execSupport(ctx, input);
    }

    public abstract CdslOutputEvent execSupport(CdslContext ctx, CdslInputEvent input) throws CdslException;


}
