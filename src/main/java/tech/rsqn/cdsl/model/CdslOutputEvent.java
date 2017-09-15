package tech.rsqn.cdsl.model;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.dsl.guards.GuardCondition;

public class CdslOutputEvent {
    public enum Action {Route, Await, Reject, End, Continue}
    private Action action;
    private CdslContext.State contextState;
    private String nextRoute;
    private String meta;
    private String contextId;

    public CdslOutputEvent withAction(Action a) {
        this.action = a;
        return this;
    }

    public CdslOutputEvent withRoute(String r) {
        this.action = Action.Route;
        this.nextRoute = r;
        return this;
    }

    public CdslOutputEvent awaitInputAt(String r) {
        this.action = Action.Await;
        this.nextRoute = r;
        return this;
    }

    public CdslOutputEvent reject(GuardCondition guardCondition) {
        this.action = Action.Reject;
        return this;
    }

    public CdslContext.State getContextState() {
        return contextState;
    }

    public void setContextState(CdslContext.State contextState) {
        this.contextState = contextState;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getNextRoute() {
        return nextRoute;
    }

    public void setNextRoute(String nextRoute) {
        this.nextRoute = nextRoute;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
}
