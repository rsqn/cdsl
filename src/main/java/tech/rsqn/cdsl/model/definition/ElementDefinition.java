package tech.rsqn.cdsl.model.definition;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementDefinition {
    public enum Classifier {FlowStep,DslElement,Configuration};

    private String id;
    private String name;
    private Classifier classifier;

    private Map<String,String> attrs = new HashMap<>();
    private List<ElementDefinition> children = new ArrayList<>();
    private String textValue;

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public List<ElementDefinition> getChildren() {
        return children;
    }

    public void setChildren(List<ElementDefinition> children) {
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }
}
