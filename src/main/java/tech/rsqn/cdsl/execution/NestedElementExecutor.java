package tech.rsqn.cdsl.execution;

import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.List;

/**
 * Executes a list of DSL elements (e.g. nested inside an "if").
 * Used by container DSLs to run their child elements with the same semantics as a step.
 */
public interface NestedElementExecutor {

    CdslOutputEvent executeElements(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent,
                                    Flow flow, FlowStep step, List<DslMetadata> elements);

    /**
     * Runs all elements in order; Route/Await/End are ignored (cannot route out).
     * Only Reject is propagated. Used by the fork container.
     */
    CdslOutputEvent executeElementsIgnoreRouteOut(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent,
                                                  Flow flow, FlowStep step, List<DslMetadata> elements);

    /**
     * Runs a single element; returns its output (caller may ignore Route/End/Await).
     * Used by fork to run each child in parallel with a dedicated runtime per task.
     */
    CdslOutputEvent executeOneElement(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent,
                                      Flow flow, FlowStep step, DslMetadata element);
}
