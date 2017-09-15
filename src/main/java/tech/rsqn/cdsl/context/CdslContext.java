package tech.rsqn.cdsl.context;

import java.util.HashMap;
import java.util.Map;

public class CdslContext {
    private String id;
    private String currentFlow;
    private String currentStep;
    private Map<String,Object> transientVars;
    private Map<String,CdslVariable> vars;

    public CdslContext() {
        vars = new HashMap<>();
        transientVars = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentFlow() {
        return currentFlow;
    }

    public void setCurrentFlow(String currentFlow) {
        this.currentFlow = currentFlow;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public Map<String, CdslVariable> getVars() {
        return vars;
    }

    public void setVars(Map<String, CdslVariable> vars) {
        this.vars = vars;
    }

    public void setVar(String k, String v) {
//        runtime.getAuditor().setVar(this,k,v,getVar(k));
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
