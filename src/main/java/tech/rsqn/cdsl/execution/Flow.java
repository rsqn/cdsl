package tech.rsqn.cdsl.execution;

import org.springframework.beans.BeanUtils;
import tech.rsqn.cdsl.definitionsource.FlowDefinition;

import java.util.HashMap;
import java.util.Map;

public class Flow {
    private String id;
    private String defaultStep;
    private String errorStep;
    private Map<String, FlowStep> steps;

    public Flow() {
        steps = new HashMap<>();
    }

    public Flow from(FlowDefinition def) {
        BeanUtils.copyProperties(def,this);
        return this;
    }

    public Map<String, FlowStep> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, FlowStep> steps) {
        this.steps = steps;
    }

    public FlowStep fetchStep(String stepId) {
        return steps.get(stepId);
    }

    public FlowStep putStep(String stepId, FlowStep step) {
        return steps.put(stepId,step);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDefaultStep() {
        return defaultStep;
    }

    public void setDefaultStep(String defaultStep) {
        this.defaultStep = defaultStep;
    }

    public String getErrorStep() {
        return errorStep;
    }

    public void setErrorStep(String errorStep) {
        this.errorStep = errorStep;
    }
}
