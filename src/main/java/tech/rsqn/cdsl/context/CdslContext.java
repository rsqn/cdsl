package tech.rsqn.cdsl.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CdslContext {
    public enum State {Undefined, Alive, Await, End, Error}

    private transient CdslRuntime runtime;

    private String id;
    private State state;
    private String currentFlow;
    private String currentStep;
    private transient Map<String, Object> transientVars;
    private Map<String, String> vars;

    public CdslContext() {
        vars = new HashMap<>();
        transientVars = new HashMap<>();
        state = State.Undefined;
    }

    public void setRuntime(CdslRuntime runtime) {
        this.runtime = runtime;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public Map<String, String> getVars() {
        return vars;
    }

    public String getVars(String n) {
        return vars.get(n);
    }

    public void setVars(Map<String, String> vars) {
        this.vars = vars;
    }

    public <T extends String> void putVar(String k, T v) {
        if ( runtime == null ) {
            throw new java.lang.RuntimeException("CdslRuntime not present");
        }
        runtime
        .getAuditor()
        .setVar(this,k,
        v != null ? v.toString() : "null",
        vars.get(k));
        vars.put(k, v);
    }

    public <T extends Serializable> T fetchVar(String k) {
        return (T) vars.get(k);
    }

    public void putTransient(String k, String v) {
        transientVars.put(k, v);
    }

    public <T> T fetchTransient(String k) {
        return (T) transientVars.get(k);
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
