package tech.rsqn.cdsl.model;

public class CdslOutputEvent {
    private String action;
    private String nextRoute;


    public CdslOutputEvent withAction(String s) {
        this.action = s;
        return this;
    }

    public CdslOutputEvent withRoute(String r) {
        this.action = "route";
        this.nextRoute = r;
        return this;
    }

    public CdslOutputEvent awaitInputAt(String r) {
        this.action = "await";
        this.nextRoute = r;
        return this;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getNextRoute() {
        return nextRoute;
    }

    public void setNextRoute(String nextRoute) {
        this.nextRoute = nextRoute;
    }
}
