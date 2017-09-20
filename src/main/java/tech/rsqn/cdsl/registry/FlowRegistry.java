package tech.rsqn.cdsl.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.execution.Flow;
import tech.rsqn.cdsl.execution.FlowStep;
import tech.rsqn.cdsl.definitionsource.ElementDefinition;
import tech.rsqn.cdsl.definitionsource.FlowDefinition;

import java.util.HashMap;
import java.util.Map;

@Component
public class FlowRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FlowRegistry.class);

    @Autowired
    private DslInitialisationHelper dslInitialisationHelper;

    private Map<String, Flow> flows;

    public FlowRegistry() {
        flows = new HashMap<>();
    }

    public Flow getFlow(String n) {
        return flows.get(n);
    }

    public void submitDefinition(FlowDefinition def) {
        Flow flow = new Flow();
        flow.from(def);

        for (ElementDefinition flowStep : def.getElements()) {
            logger.info("Registering FlowStep : (" + flowStep.getId() + ")");

            FlowStep step = new FlowStep().from(flowStep);

            for (ElementDefinition element : flowStep.getChildren()) {
                if (element.getName().equals("finally")) {
                    for (ElementDefinition finalElement : element.getChildren()) {
                        logger.info("Registering finally block DSL (" + flowStep.getId() + "." + element.getName() + ")");
                        DslMetadata meta = dslInitialisationHelper.buildMetadataForDslElement(flowStep, finalElement);
                        dslInitialisationHelper.validate(meta);
                        step.addFinalElement(meta);

                    }
                } else {
                    logger.info("Registering DSL (" + flowStep.getId() + "." + element.getName() + ")");
                    DslMetadata meta = dslInitialisationHelper.buildMetadataForDslElement(flowStep, element);
                    dslInitialisationHelper.validate(meta);
                    step.addLogicElement(meta);
                }

            }
            flow.putStep(flowStep.getId(), step);
        }

        flows.put(def.getId(), flow);
    }

}
