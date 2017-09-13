package tech.rsqn.cdsl.model.definition;


import java.util.ArrayList;
import java.util.List;

public class FlowDefinition {

    private String id;

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
}
