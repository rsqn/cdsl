package tech.rsqn.cdsl.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.model.Flow;
import tech.rsqn.cdsl.model.FlowStep;
import tech.rsqn.cdsl.model.definition.ElementDefinition;
import tech.rsqn.cdsl.model.definition.FlowDefinition;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlowRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FlowRegistry.class);

    @Autowired
    private DslRegistry dslRegistry;

    private Map<String,Flow> flows;

    private Map<String,FlowStep> flowSteps;


    public FlowRegistry() {
        flows = new HashMap<>();
        flowSteps = new HashMap<>();
    }

    public Flow getFlow(String n) {
        return flows.get(n);
    }

    public FlowStep getFlowStep(String n) {
        return flowSteps.get(n);
    }

    public void submitDefinition(FlowDefinition def) {
        for (ElementDefinition flowStep : def.getElements()) {
            FlowStep step = new FlowStep().from(flowStep);
            boolean inFinalBlock = false;

            for (ElementDefinition element : flowStep.getChildren()) {
                if ( element.getName().equals("finally")) {
                    for (ElementDefinition finalElement : element.getChildren()) {
                        DslMetadata meta = dslRegistry.registerDslDefinition(flowStep,finalElement);
                        step.addFinalElement(meta);
                    }
                } else {
                    DslMetadata meta = dslRegistry.registerDslDefinition(flowStep,element);
                    step.addLogicElement(meta);
                }

            }
            flowSteps.put(flowStep.getId(),step);
        }

        flows.put(def.getId(),new Flow().from(def));
    }

}
