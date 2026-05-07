package tech.rsqn.cdsl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.rsqn.cdsl.dsl.AwaitModel;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.dsl.RouteToModel;
import tech.rsqn.cdsl.dsl.WhitelistEventsModel;
import tech.rsqn.cdsl.exceptions.CdslValidationException;
import tech.rsqn.cdsl.execution.Flow;
import tech.rsqn.cdsl.execution.FlowStep;
import tech.rsqn.cdsl.registry.FlowRegistry;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

@Component
public class FlowValidator {

    public void validateAll(FlowRegistry registry) throws CdslValidationException {
        if (registry == null || registry.getFlows() == null) {
            throw new CdslValidationException("FlowRegistry is not available for validation");
        }
        for (Flow flow : registry.getFlows().values()) {
            validate(flow);
        }
    }

    public void validate(Flow flow) throws CdslValidationException {
        if (flow == null) {
            throw new CdslValidationException("Flow cannot be null");
        }
        if (StringUtils.isBlank(flow.getId())) {
            throw new CdslValidationException("Flow id cannot be blank");
        }
        if (StringUtils.isBlank(flow.getDefaultStep())) {
            throw new CdslValidationException("Flow '" + flow.getId() + "' must define defaultStep");
        }
        if (flow.fetchStep(flow.getDefaultStep()) == null) {
            throw new CdslValidationException("Flow '" + flow.getId() + "' defaultStep '" + flow.getDefaultStep() + "' does not exist");
        }
        if (StringUtils.isNotBlank(flow.getErrorStep()) && flow.fetchStep(flow.getErrorStep()) == null) {
            throw new CdslValidationException("Flow '" + flow.getId() + "' errorStep '" + flow.getErrorStep() + "' does not exist");
        }

        Map<String, Set<String>> edges = new HashMap<>();
        for (Map.Entry<String, FlowStep> e : flow.getSteps().entrySet()) {
            String stepId = e.getKey();
            FlowStep step = e.getValue();
            Set<String> targets = new HashSet<>();

            collectTargets(flow, stepId, step.getLogicElements(), targets);
            collectTargets(flow, stepId, step.getFinalElements(), targets);

            edges.put(stepId, targets);
        }

        // Reachability: every step must be reachable from defaultStep by following routeTo/await edges.
        Set<String> reachable = new HashSet<>();
        Queue<String> q = new ArrayDeque<>();
        q.add(flow.getDefaultStep());
        reachable.add(flow.getDefaultStep());
        if (StringUtils.isNotBlank(flow.getErrorStep())) {
            // errorStep is reachable via exception routing; do not require an explicit routeTo edge
            q.add(flow.getErrorStep());
            reachable.add(flow.getErrorStep());
        }
        while (!q.isEmpty()) {
            String cur = q.remove();
            for (String nxt : edges.getOrDefault(cur, Set.of())) {
                if (!reachable.contains(nxt)) {
                    reachable.add(nxt);
                    q.add(nxt);
                }
            }
        }

        for (String stepId : flow.getSteps().keySet()) {
            if (!reachable.contains(stepId)) {
                throw new CdslValidationException("Flow '" + flow.getId() + "' has unreachable step '" + stepId + "'");
            }
        }
    }

    private void collectTargets(Flow flow, String stepId, Iterable<DslMetadata> elements, Set<String> outTargets) throws CdslValidationException {
        if (elements == null) {
            return;
        }
        for (DslMetadata meta : elements) {
            if (meta == null) {
                continue;
            }
            String name = meta.getName();
            if ("routeTo".equals(name)) {
                Object model = meta.getModel();
                if (!(model instanceof RouteToModel)) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' routeTo has unexpected model: " + (model == null ? "null" : model.getClass()));
                }
                String target = ((RouteToModel) model).getTarget();
                if (StringUtils.isBlank(target)) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' routeTo target cannot be blank");
                }
                if (flow.fetchStep(target) == null) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' routes to missing step '" + target + "'");
                }
                outTargets.add(target);
            } else if ("await".equals(name)) {
                Object model = meta.getModel();
                if (!(model instanceof AwaitModel)) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' await has unexpected model: " + (model == null ? "null" : model.getClass()));
                }
                String at = ((AwaitModel) model).getAt();
                if (StringUtils.isBlank(at)) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' await at cannot be blank");
                }
                if (flow.fetchStep(at) == null) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' awaits at missing step '" + at + "'");
                }
                outTargets.add(at);
            } else if ("whiteList".equals(name)) {
                Object model = meta.getModel();
                if (!(model instanceof WhitelistEventsModel)) {
                    throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' whiteList has unexpected model: " + (model == null ? "null" : model.getClass()));
                }
                WhitelistEventsModel wm = (WhitelistEventsModel) model;
                if ("Route".equals(wm.getOnFailure())) {
                    String to = wm.getTo();
                    if (StringUtils.isBlank(to)) {
                        throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' whiteList to cannot be blank when onFailure=Route");
                    }
                    if (flow.fetchStep(to) == null) {
                        throw new CdslValidationException("Flow '" + flow.getId() + "' step '" + stepId + "' whiteList routes to missing step '" + to + "'");
                    }
                    outTargets.add(to);
                }
            }

            if (meta.getChildElements() != null) {
                collectTargets(flow, stepId, meta.getChildElements(), outTargets);
            }
        }
    }
}
