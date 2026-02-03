package tech.rsqn.cdsl.dsl;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.execution.Flow;
import tech.rsqn.cdsl.execution.FlowStep;
import tech.rsqn.cdsl.execution.NestedElementExecutor;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.List;

/**
 * Base for DSLs that run nested/child elements (e.g. if, foreach).
 * Subclasses decide when and how often to run the body; this class provides
 * the shared logic to execute the current element's child DSL list.
 */
public abstract class AbstractNestedDsl<T, MT extends Serializable> extends DslSupport<T, MT> {

    /**
     * Runs the nested child elements for the current DSL element.
     * Uses runtime's currentElementMetadata child list and nestedElementExecutor.
     *
     * @return the first non-null output from a child, or null if all ran without output
     */
    protected CdslOutputEvent runNestedElements(CdslRuntime runtime, CdslContext ctx,
                                                CdslInputEvent<MT> input) throws CdslException {
        List<DslMetadata> children = runtime.getCurrentElementMetadata() != null
                ? runtime.getCurrentElementMetadata().getChildElements()
                : null;
        if (children == null || children.isEmpty()) {
            return null;
        }
        NestedElementExecutor executor = runtime.getNestedElementExecutor();
        if (executor == null) {
            throw new CdslException("NestedElementExecutor not set on runtime - cannot run nested body");
        }
        Flow flowRef = runtime.getCurrentFlow();
        FlowStep stepRef = runtime.getCurrentStep();
        if (flowRef == null || stepRef == null) {
            throw new CdslException("Current flow/step not set on runtime - cannot run nested body");
        }
        return executor.executeElements(runtime, ctx, input, flowRef, stepRef, children);
    }
}
