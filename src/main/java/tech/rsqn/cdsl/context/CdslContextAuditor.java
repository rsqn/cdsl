package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.model.CdslAuditEvent;

public class CdslContextAuditor {

    public void setVar(CdslContext ctx, String k, String v, CdslVariable f) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentState(), "var");
        ev.setAttr(k);
        ev.setFrom(f != null ? f.toString(): null);
        ev.setTo(v);
        System.out.println(ev);
    }

    public void transition(CdslContext ctx, String to) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentState(), "transition");
        ev.setFrom(ctx.getCurrentState());
        ev.setTo(to);
        System.out.println(ev);
    }

    public void reject(CdslContext ctx, String msg) {
        CdslAuditEvent ev = new CdslAuditEvent().with(ctx.getCurrentState(), "reject");
        ev.setFrom(msg);
        ev.setTo(ctx.getCurrentState());
        System.out.println(ev);
    }
}
