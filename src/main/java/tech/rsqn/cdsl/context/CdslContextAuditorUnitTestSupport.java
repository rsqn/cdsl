package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.execution.PostCommitTask;
import tech.rsqn.cdsl.execution.PostStepTask;
import tech.rsqn.cdsl.model.CdslAuditEvent;

import java.util.ArrayList;
import java.util.List;

public class CdslContextAuditorUnitTestSupport implements CdslContextAuditor {

    private List<String> events = new ArrayList<>();

    public boolean didLogEvent(String s) {
        return events.contains(s);
    }
    
    @Override
    public String toString() {
        String s = "CdslContextAuditorUnitTestSupport.events:";
        for (String event : events) {
           s += event + "\n";
        }
        s += "\n\n";
        return s;
    }

    @Override
    public void error(CdslContext ctx, String flow, String step, String dsl, Throwable throwable) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "error");
//        ev.setAttr(k);
//        ev.setFrom(f != null ? f.toString(): null);
//        ev.setTo(v);
        System.out.println(ev);
    }

    @Override
    public void executePostStep(CdslContext ctx, String flow, String step, PostStepTask p) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "postStep");
//        ev.setAttr(k);
//        ev.setFrom(f != null ? f.toString(): null);
//        ev.setTo(v);
        System.out.println(ev);
    }

    @Override
    public void executePostCommit(CdslContext ctx, String flow, PostCommitTask p) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "postCommit");
//        ev.setAttr(k);
//        ev.setFrom(f != null ? f.toString(): null);
//        ev.setTo(v);
        System.out.println(ev);
    }

    public void setVar(CdslContext ctx, String k, String v, Object f) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "var");
        ev.setAttr(k);
        ev.setFrom(f != null ? f.toString(): null);
        ev.setTo(v);

        events.add("setVar/" + k);


        System.out.println(ev);
    }

    public void execute(CdslContext ctx, String flow, String step, String dsl) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "execute");
        ev.setFrom(ctx.getCurrentStep());
        ev.setTo("execute/" + flow + "." + step + "." + dsl);
        events.add(ev.getTo());
        System.out.println(ev);
    }

    public void transition(CdslContext ctx, String flow, String toStep) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "transition");
        ev.setFrom(ctx.getCurrentStep());
        ev.setTo(flow + "." + toStep);
        events.add("transition/" + ev.getTo());
        System.out.println(ev);
    }

    public void reject(CdslContext ctx, String msg) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentStep(), "reject");
        ev.setFrom(msg);
        ev.setTo(ctx.getCurrentStep());
        System.out.println(ev);
    }
    
    public void clear() {
      events.clear();
    }
}
