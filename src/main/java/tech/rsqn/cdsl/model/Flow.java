package tech.rsqn.cdsl.model;

import org.springframework.beans.BeanUtils;
import tech.rsqn.cdsl.model.definition.FlowDefinition;

public class Flow {
    private String id;
    private String defaultStep;
    private String errorStep;

    public Flow from(FlowDefinition def) {
        BeanUtils.copyProperties(def,this);
        return this;
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
