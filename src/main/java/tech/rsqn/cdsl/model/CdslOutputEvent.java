package tech.rsqn.cdsl.model;

import tech.rsqn.cdsl.dsl.guards.GuardCondition;

public class CdslOutputEvent {
    public enum Action {Route,Await,Reject}
    private Action action;
    private String nextRoute;
    private String meta;

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
