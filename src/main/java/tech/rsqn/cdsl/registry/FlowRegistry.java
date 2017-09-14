package tech.rsqn.cdsl.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.model.definition.ElementDefinition;
import tech.rsqn.cdsl.model.definition.FlowDefinition;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlowRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FlowRegistry.class);
    private Map<String,FlowDefinition> flows;

    @Autowired
    private DslRegistry dslRegistry;

    public FlowRegistry() {
        flows = new HashMap<>();
    }

    public FlowDefinition getFlow(String n) {
        return flows.get(n);
    }

    public void submitDefinition(FlowDefinition def) {
        for (ElementDefinition flowStep : def.getElements()) {
            for (ElementDefinition dsl : flowStep.getChildren()) {
                dslRegistry.registerDslDefinition(flowStep,dsl);
            }
        }

        flows.put(def.getId(),def);
    }

}
