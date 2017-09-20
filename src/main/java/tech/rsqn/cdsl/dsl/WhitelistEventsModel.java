package tech.rsqn.cdsl.dsl;

import java.util.ArrayList;
import java.util.List;

public class WhitelistEventsModel {
    private String onFailure;
    private String to;
    private String names;

    public String getOnFailure() {
        return onFailure;
    }

    public void setOnFailure(String onFailure) {
        this.onFailure = onFailure;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }
}
