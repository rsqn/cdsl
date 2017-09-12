package tech.rsqn.cdsl.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import tech.rsqn.cdsl.flows.CdslFlow;

import java.util.List;

@XStreamAlias("flow")
public class FlowDefinition {

    @XStreamAsAttribute
    private String name;

    @XStreamImplicit
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
