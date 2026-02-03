package tech.rsqn.cdsl.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.annotations.CdslDef;
import tech.rsqn.cdsl.annotations.CdslModel;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.execution.Flow;
import tech.rsqn.cdsl.execution.FlowStep;
import tech.rsqn.cdsl.execution.NestedElementExecutor;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Nested container: runs all child elements in parallel within the same step.
 * Route/Await/End from children are ignored (you cannot route out of the step from inside a fork).
 * Only Reject is propagated. Same context is shared (thread-safe); each child runs with its own CdslRuntime.
 */
@CdslDef("fork")
@CdslModel(MapModel.class)
@Component
public class Fork extends DslSupport<MapModel, Serializable> {
    private static final Logger logger = LoggerFactory.getLogger(Fork.class);

    private static final ExecutorService PARALLEL_POOL = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors()));

    @Override
    public CdslOutputEvent execSupport(CdslRuntime runtime, CdslContext ctx, MapModel model, CdslInputEvent input) throws CdslException {
        List<DslMetadata> children = runtime.getCurrentElementMetadata() != null
                ? runtime.getCurrentElementMetadata().getChildElements()
                : null;
        if (children == null || children.isEmpty()) {
            return null;
        }
        NestedElementExecutor executor = runtime.getNestedElementExecutor();
        if (executor == null) {
            throw new CdslException("NestedElementExecutor not set on runtime - cannot run fork body");
        }
        Flow flowRef = runtime.getCurrentFlow();
        FlowStep stepRef = runtime.getCurrentStep();
        if (flowRef == null || stepRef == null) {
            throw new CdslException("Current flow/step not set on runtime - cannot run fork body");
        }

        List<Future<CdslOutputEvent>> futures = new ArrayList<>();
        for (DslMetadata child : children) {
            CdslRuntime childRuntime = new CdslRuntime();
            childRuntime.setAuditor(runtime.getAuditor());
            childRuntime.setNestedElementExecutor(executor);
            childRuntime.setCurrentFlow(flowRef);
            childRuntime.setCurrentStep(stepRef);

            DslMetadata childMeta = child;
            futures.add(PARALLEL_POOL.submit(new Callable<CdslOutputEvent>() {
                @Override
                public CdslOutputEvent call() {
                    return executor.executeOneElement(childRuntime, ctx, input, flowRef, stepRef, childMeta);
                }
            }));
        }

        CdslOutputEvent rejectResult = null;
        for (Future<CdslOutputEvent> f : futures) {
            try {
                CdslOutputEvent out = f.get();
                if (out != null && out.getAction() == CdslOutputEvent.Action.Reject) {
                    rejectResult = out;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CdslException("Fork interrupted", e);
            } catch (ExecutionException e) {
                throw new CdslException("Fork branch failed", e.getCause());
            }
        }
        return rejectResult;
    }
}
