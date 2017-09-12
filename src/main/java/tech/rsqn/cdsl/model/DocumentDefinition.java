package tech.rsqn.cdsl.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

@XStreamAlias("cdsl")
public class DocumentDefinition {

    @XStreamImplicit
    private List<FlowDefinition> flows;

    public List<FlowDefinition> getFlows() {
        return flows;
    }

    public void setFlows(List<FlowDefinition> flows) {
        this.flows = flows;
    }
}
