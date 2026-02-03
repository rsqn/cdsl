package tech.rsqn.cdsl.execution;

import com.esotericsoftware.kryo.Kryo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tech.rsqn.cdsl.concurrency.Lock;
import tech.rsqn.cdsl.concurrency.LockProvider;
import tech.rsqn.cdsl.concurrency.LockRejectedException;
import tech.rsqn.cdsl.context.CdslContext;
import tech.rsqn.cdsl.context.CdslContextAuditor;
import tech.rsqn.cdsl.context.CdslContextRepository;
import tech.rsqn.cdsl.context.CdslRuntime;
import tech.rsqn.cdsl.dsl.Dsl;
import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.exceptions.CdslException;
import tech.rsqn.cdsl.model.CdslFlowOutputEvent;
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputValue;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.registry.DslInitialisationHelper;
import tech.rsqn.cdsl.registry.FlowRegistry;
import tech.rsqn.useful.things.identifiers.UIDHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowExecutor implements NestedElementExecutor {
    private static final Logger logger = LoggerFactory.getLogger(FlowExecutor.class);
    private Kryo kryo = new Kryo();

    @Autowired
    private FlowRegistry flowRegistry;

    @Autowired
    private DslInitialisationHelper dslInitialisationHelper;

    @Autowired
    private LockProvider lockProvider;

    @Autowired
    private CdslContextAuditor auditor;

    @Autowired
    private CdslContextRepository contextRepository;

    private int lockRetries = 3;

    private long lockDuration = 30L * 1000L;

    private long lockRetryMaxDuration = 1000L;

    private String myIdentifier = "<anonymous>";

    public void setLockRetries(int lockRetries) {
        this.lockRetries = lockRetries;
    }

    public void setLockDuration(long lockDuration) {
        this.lockDuration = lockDuration;
    }

    public void setLockRetryMaxDuration(long lockRetryMaxDuration) {
        this.lockRetryMaxDuration = lockRetryMaxDuration;
    }

    public void setMyIdentifier(String myIdentifier) {
        this.myIdentifier = myIdentifier;
    }

    public void setFlowRegistry(FlowRegistry flowRegistry) {
        this.flowRegistry = flowRegistry;
    }

    public void setDslInitialisationHelper(DslInitialisationHelper dslInitialisationHelper) {
        this.dslInitialisationHelper = dslInitialisationHelper;
    }

    public void setLockProvider(LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    public void setContextRepository(CdslContextRepository contextRepository) {
        this.contextRepository = contextRepository;
    }

    private Object intersectModel(Object src) {
        Object ret = kryo.copy(src);
        return ret;
    }

    @Override
    public CdslOutputEvent executeElements(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent,
                                           Flow flow, FlowStep step, List<DslMetadata> elements) {
        return obtainOutputs(runtime, context, inputEvent, flow, step, elements);
    }

    private CdslOutputEvent obtainOutputs(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent, Flow flow, FlowStep step, List<DslMetadata> elements) {

        for (DslMetadata dslMeta : elements) {
            String ll = step.getId() + "." + dslMeta.getName();
            runtime.getAuditor().execute(context, flow.getId(), step.getId(), dslMeta.getName());

            logger.debug("Executing " + ll);
            Dsl dsl = dslInitialisationHelper.resolve(dslMeta);

            // build or intersect model
            Object model = intersectModel(dslMeta.getModel());

            runtime.setCurrentElementMetadata(dslMeta);
            try {
                // execute the step
                CdslOutputEvent output = dsl.execute(runtime, context, model, inputEvent);

                // handle output if required
                if (output != null) {
                    logger.debug("DSL " + ll + " interim output  " + output.toString());
                    logger.debug("DSL " + ll + " output received - breaking");
                    return output;
                } else {
                    logger.debug("DSL " + ll + " no output - continue to next step");
                }
            } finally {
                runtime.setCurrentElementMetadata(null);
            }
        }

        return null;
    }

    /**
     * Runs all elements in order; Route/Await/End are ignored (cannot route out of this block).
     * Only Reject is propagated. Used by the fork container.
     */
    @Override
    public CdslOutputEvent executeElementsIgnoreRouteOut(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent,
                                                         Flow flow, FlowStep step, List<DslMetadata> elements) {
        for (DslMetadata dslMeta : elements) {
            CdslOutputEvent output = executeOneElement(runtime, context, inputEvent, flow, step, dslMeta);
            if (output != null && output.getAction() == CdslOutputEvent.Action.Reject) {
                return output;
            }
        }
        return null;
    }

    @Override
    public CdslOutputEvent executeOneElement(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent,
                                             Flow flow, FlowStep step, DslMetadata element) {
        return obtainOutputs(runtime, context, inputEvent, flow, step, Collections.singletonList(element));
    }

    public CdslFlowOutputEvent execute(Flow flow, CdslInputEvent inputEvent) {
        if (flow == null) {
            throw new RuntimeException("Flow must be provided");
        }
        Lock lock = null;
        CdslContext context = null;
        CdslRuntime runtime = null;

        try {
            if (StringUtils.isEmpty(inputEvent.getContextId())) {
                // create a new context
                context = new CdslContext();
                context.setId(UIDHelper.generate());
                lock = lockProvider.obtain(myIdentifier, "context/" + context.getId(), lockDuration, lockRetries, lockRetryMaxDuration);
                contextRepository.saveContext(lock.getId(), context);
                context = contextRepository.getContext(lock.getId(), context.getId());
            } else {
                // lock and load an existing context
                lock = lockProvider.obtain(myIdentifier, "context/" + inputEvent.getContextId(), lockDuration, lockRetries, lockRetryMaxDuration);
                context = contextRepository.getContext(lock.getId(), inputEvent.getContextId());

                if (CdslContext.State.End == context.getState()) {
                    throw new CdslException("State of " + context.getId() + " is End");
                }
            }

            // get or determine current step
            if (StringUtils.isEmpty(context.getCurrentStep())) {
                context.setCurrentStep(flow.getDefaultStep());
                logger.debug(context.getId() + " state is empty, using default defaultStep " + flow.getDefaultStep());
            }

            runtime = new CdslRuntime();
            runtime.setAuditor(auditor);
            runtime.setTransactionId(lock.getId());
            runtime.setNestedElementExecutor(this);
            context.setRuntime(runtime);

            // get the step
            FlowStep step = null;
            FlowStep nextStep = flow.fetchStep(context.getCurrentStep());
            CdslFlowOutputEvent outputEvent = null;

            if (StringUtils.isNotEmpty(inputEvent.getRequestedStep())) {
                logger.debug(context.getId() + " inputEvent is requesting step " + inputEvent.getRequestedStep());
                nextStep = flow.fetchStep(inputEvent.getRequestedStep());
                if (nextStep == null) {
                    logger.warn("Requested step " + inputEvent.getRequestedStep() + " was not found");
                }
                // todo - check guard conditions
                context.setCurrentStep(inputEvent.getRequestedStep());
            }

            Map<String, CdslOutputValue> outputValues = new HashMap<>();

            while (nextStep != null) {
                context.setCurrentStep(nextStep.getId());
                context.pushTransition(flow.getId() + "/" + nextStep.getId());
                runtime.getAuditor().transition(context, flow.getId(), nextStep.getId());

                step = nextStep;
                runtime.setCurrentFlow(flow);
                runtime.setCurrentStep(step);
                nextStep = null;
                String logPrefix = step.getId();

                logger.debug("Executing " + step.getId());
                try {
                    CdslOutputEvent result;
                    CdslOutputEvent generalOutput = obtainOutputs(runtime, context, inputEvent, flow, step, step.getLogicElements());
                    CdslOutputEvent finalOutput = obtainOutputs(runtime, context, inputEvent, flow, step, step.getFinalElements());

                    if (finalOutput != null) {
                        logger.debug("Result of  " + step.getId() + " provided by final group DSL group");
                        result = finalOutput;
                        // final output overrides general output for results
                    } else if (generalOutput != null) {
                        logger.debug("Result of  " + step.getId() + " provided by general DSL group category");
                        result = generalOutput;
                    } else {
                        logger.debug("Result of  " + step.getId() + " was null");
                        result = null;
                    }

                    // execute post step tasks
                    for (PostStepTask postStepTask : runtime.getPostStepTasks()) {
                        try {
                            auditor.executePostStep(context, flow.getId(), step.getId(), postStepTask);
                            postStepTask.runTask();
                        } catch (Exception ex) {
                            runtime.getAuditor().error(context, flow.getId(), step.getId(), null, ex);
                            logger.debug(logPrefix + " caught exception in post step task - ignoring " + ex.getMessage(), ex);
                        }
                    }
                    runtime.getPostStepTasks().clear();

                    //todo - clean up where state is set
                    if (CdslOutputEvent.Action.Route == result.getAction()) {
                        logger.debug(logPrefix + " routing to " + result.getNextRoute());
                        context.setCurrentStep(result.getNextRoute());
                        nextStep = flow.fetchStep(result.getNextRoute());
                        if (nextStep == null) {
                            throw new CdslException("Invalid Route " + result.getNextRoute());
                        }
                    } else if (CdslOutputEvent.Action.Await == result.getAction()) {
                        logger.debug(logPrefix + " awaiting at " + result.getNextRoute());
                        context.setState(CdslContext.State.Await);
                        context.setCurrentStep(result.getNextRoute());
                    } else if (CdslOutputEvent.Action.End == result.getAction()) {
                        logger.debug(logPrefix + " end at " + result.getNextRoute());
                    } else if (CdslOutputEvent.Action.Reject == result.getAction()) {
                        logger.debug(logPrefix + " rejected" + result.getNextRoute());
                    }
                    outputEvent = new CdslFlowOutputEvent().with(result);


                } catch (Exception ex) {
                    logger.warn("Exception Caught " + ex.getMessage() + " - routing to exception handling step " + flow.getErrorStep(), ex);
                    runtime.getAuditor().error(context, flow.getId(), step.getId(), null, ex);
                    if (StringUtils.isNotEmpty(flow.getErrorStep())) {
                        nextStep = flow.fetchStep(flow.getErrorStep());
                    } else {
                        throw new CdslException(ex);
                    }
                }

            }

            // save context
            contextRepository.saveContext(runtime.getTransactionId(), context);

            // release lock
            lockProvider.release(lock);
            lock = null;

            // execute post txn tasks
            for (PostCommitTask postCommitTask : runtime.getPostCommitTasks()) {
                try {
                    auditor.executePostCommit(context, flow.getId(), postCommitTask);
                    postCommitTask.runTask();
                } catch (Exception ex) {
                    logger.debug(flow.getId() + " caught exception in post commit task - ignoring " + ex.getMessage(), ex);
                }
            }
            runtime.getPostCommitTasks().clear();

            // output something
            if (outputEvent == null) {
                outputEvent = new CdslFlowOutputEvent();
            }

            outputEvent.setContextId(context.getId());
            outputEvent.setContextState(context.getState());
            // perhaps we want to honour the order in which conflicting keys were set here.
            outputEvent.getOutputValues().putAll(outputValues);

            outputEvent.setOutputValues(runtime.getOutputValueMap());

            runtime = null;

            return outputEvent;

        } catch (LockRejectedException lockRejected) {
            logger.warn("Lock Rejected ", lockRejected);
        } finally {
//            if ( runtime != null ) {
//                contextRepository.saveContext(runtime.getTransactionId(),context);
//            }
            if (lock != null)
                lockProvider.release(lock);
        }

        return null;
    }

}
