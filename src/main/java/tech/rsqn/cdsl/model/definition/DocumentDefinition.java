package tech.rsqn.cdsl.model.definition;


import java.util.ArrayList;
import java.util.List;

public class DocumentDefinition {

    private List<FlowDefinition> flows = new ArrayList<>();

    public List<FlowDefinition> getFlows() {
        return flows;
    }

    public void setFlows(List<FlowDefinition> flows) {
        this.flows = flows;
    }
}
