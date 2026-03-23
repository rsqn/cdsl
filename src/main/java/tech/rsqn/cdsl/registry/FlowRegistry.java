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

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class FlowRegistry {
    private static final Logger logger = LoggerFactory.getLogger(FlowRegistry.class);

    /** Tag names that are nested container DSLs (have executable child elements). */
    private static final Set<String> NESTED_CONTAINER_NAMES = new HashSet<>();
    static {
        NESTED_CONTAINER_NAMES.add("if");
        NESTED_CONTAINER_NAMES.add("foreach");
        NESTED_CONTAINER_NAMES.add("parallel");
    }

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
                } else if (NESTED_CONTAINER_NAMES.contains(element.getName())) {
                    DslMetadata meta = buildNestedContainerMeta(flowStep, element);
                    step.addLogicElement(meta);
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

    /**
     * Recursively builds {@link DslMetadata} for a nested container element
     * (e.g. {@code foreach}, {@code if}).
     *
     * <p>The container's model is built from attributes only (children are stripped
     * to prevent {@code DslModelBuilder} from trying to map child DSL elements as
     * model fields).  Each child element is then processed recursively so that
     * nested containers (e.g. {@code <if>} inside {@code <foreach>}) are handled
     * correctly at any depth.
     *
     * @param flowStep the parent step element (used for logging and model building)
     * @param element  the nested container element to register
     * @return fully populated {@link DslMetadata} for the container
     */
    private DslMetadata buildNestedContainerMeta(ElementDefinition flowStep, ElementDefinition element) {
        // Build model from attributes only — children are executable DSLs, not model fields
        ElementDefinition attrsOnly = ElementDefinition.copyWithoutChildren(element);
        DslMetadata meta = dslInitialisationHelper.buildMetadataForDslElement(flowStep, attrsOnly);
        for (ElementDefinition child : element.getChildren()) {
            DslMetadata childMeta;
            if (NESTED_CONTAINER_NAMES.contains(child.getName())) {
                // Recurse: child is itself a nested container (e.g. <if> inside <foreach>)
                childMeta = buildNestedContainerMeta(flowStep, child);
            } else {
                childMeta = dslInitialisationHelper.buildMetadataForDslElement(flowStep, child);
                dslInitialisationHelper.validate(childMeta);
            }
            meta.addChildElement(childMeta);
        }
        dslInitialisationHelper.validate(meta);
        return meta;
    }

}
