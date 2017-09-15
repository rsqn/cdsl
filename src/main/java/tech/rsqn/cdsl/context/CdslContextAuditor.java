package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.execution.PostCommitTask;
import tech.rsqn.cdsl.execution.PostStepTask;

public interface CdslContextAuditor {

    void setVar(CdslContext ctx, String k, String v, CdslVariable f);

    void error(CdslContext ctx, String flow, String step, String dsl, Throwable throwable);

    void execute(CdslContext ctx, String flow, String step, String dsl);

    void executePostStep(CdslContext ctx, String flow, String step, PostStepTask p);

    void executePostCommit(CdslContext ctx, String flow, PostCommitTask p);

    void transition(CdslContext ctx, String flow, String toStep);

    void reject(CdslContext ctx, String msg);
}
