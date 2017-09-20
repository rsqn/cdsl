package tech.rsqn.cdsl.definitionsource;


import java.util.ArrayList;
import java.util.List;

public class FlowDefinition {

    private String id;
    private String defaultStep;
    private String errorStep;

    private List<ElementDefinition> elements = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ElementDefinition> getElements() {
        return elements;
    }

    public void setElements(List<ElementDefinition> elements) {
        this.elements = elements;
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
