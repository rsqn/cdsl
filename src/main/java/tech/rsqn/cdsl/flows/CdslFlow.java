package tech.rsqn.cdsl.flows;

import tech.rsqn.cdsl.model.definition.ElementDefinition;

import java.util.List;

public class CdslFlow {
    private String name;
    private List<ElementDefinition> elements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ElementDefinition> getElements() {
        return elements;
    }

    public void setElements(List<ElementDefinition> elements) {
        this.elements = elements;
    }
}
