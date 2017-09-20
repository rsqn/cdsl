package tech.rsqn.cdsl.execution;

import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.definitionsource.ElementDefinition;
import tech.rsqn.reflectionhelpers.ReflectionHelper;

import java.util.ArrayList;
import java.util.List;

public class FlowStep {
    private String id;
    private List<DslMetadata> logicElements;
    private List<DslMetadata> finalElements;

    public FlowStep() {
        logicElements = new ArrayList<>();
        finalElements = new ArrayList<>();
    }

    public void addLogicElement(DslMetadata meta) {
        logicElements.add(meta);
    }

    public void addFinalElement(DslMetadata meta) {
        finalElements.add(meta);
    }

    public FlowStep from(ElementDefinition def) {
        for (String key : def.getAttrs().keySet()) {
            ReflectionHelper.putAttribute(this,key,def.getAttrs().get(key));
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<DslMetadata> getLogicElements() {
        return logicElements;
    }

    public void setLogicElements(List<DslMetadata> logicElements) {
        this.logicElements = logicElements;
    }

    public List<DslMetadata> getFinalElements() {
        return finalElements;
    }

    public void setFinalElements(List<DslMetadata> finalElements) {
        this.finalElements = finalElements;
    }
}
