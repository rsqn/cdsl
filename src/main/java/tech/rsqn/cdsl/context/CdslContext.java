package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.HashMap;
import java.util.Map;

public class CdslContext {
    private String currentState;
    private transient CdslContextAuditor auditor;
//    private yolo getRuntime?

    public void setAuditor(CdslContextAuditor auditor) {
        this.auditor = auditor;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    private Map<String,CdslVariable> vars;

    public CdslContext() {
        vars = new HashMap<>();
    }

    public Map<String, CdslVariable> getVars() {
        return vars;
    }

    public void setVars(Map<String, CdslVariable> vars) {
        this.vars = vars;
    }

    public void setVar(String k, String v) {
        auditor.setVar(this,k,v,getVar(k));
        vars.put(k,new CdslVariable().with(v));
    }

    public <T> T getVar(String k) {
        CdslVariable v = vars.get(k);
        if ( v != null ) {
            return (T) v.getV();
        }
        return null;
    }

    /*
    public void setVar(String k, Number v) {
        vars.put(k,new CdslVariable().with(k,v));
    }

    public void setVar(String k, Boolean v) {
        vars.put(k,new CdslVariable().with(k,v));
    }

    public void setVar(String k, Collection v) {
        vars.put(k,new CdslVariable().with(k,v));
    }

    public void setVar(String k, Map v) {
        vars.put(k,new CdslVariable().with(k,v));
    }
    */
}
