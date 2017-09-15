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
import tech.rsqn.cdsl.model.CdslInputEvent;
import tech.rsqn.cdsl.model.CdslOutputEvent;
import tech.rsqn.cdsl.model.Flow;
import tech.rsqn.cdsl.model.FlowStep;
import tech.rsqn.cdsl.registry.DslRegistry;
import tech.rsqn.cdsl.registry.FlowRegistry;
import tech.rsqn.useful.things.identifiers.UIDHelper;

import java.util.List;

public class FlowExecutor {
    private static final Logger logger = LoggerFactory.getLogger(FlowExecutor.class);
    private Kryo kryo = new Kryo();

    @Autowired
    private FlowRegistry flowRegistry;

    @Autowired
    private DslRegistry dslRegistry;

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

    public void setDslRegistry(DslRegistry dslRegistry) {
        this.dslRegistry = dslRegistry;
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

    private CdslOutputEvent obtainOutputs(CdslRuntime runtime, CdslContext context, CdslInputEvent inputEvent, Flow flow, FlowStep step, List<DslMetadata> elements) {

        for (DslMetadata dslMeta : elements) {
            String ll = step.getId() + "." + dslMeta.getName();
            runtime.getAuditor().execute(context, flow.getId(), step.getId(), dslMeta.getName());

            logger.debug("Executing " + ll);
            Dsl dsl = dslRegistry.resolve(dslMeta);

            // build or intersect model
            Object model = intersectModel(dslMeta.getModel());

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
        }

        return null;
    }

    public CdslOutputEvent execute(Flow flow, CdslInputEvent inputEvent) {
        if (flow == null) {
            throw new RuntimeException("Flow must be provided");
        }
        Lock lock = null;
        CdslContext context = null;

        try {
            if (StringUtils.isEmpty(inputEvent.getContextId())) {
                // create a new context
                context = new CdslContext();
                context.setId(UIDHelper.generate());
                lock = lockProvider.obtain(myIdentifier, "context/" + context.getId(), lockDuration, lockRetries, lockRetryMaxDuration);
                contextRepository.saveContext(context);
            } else {
                // lock and load an existing context
                lock = lockProvider.obtain(myIdentifier, "context/" + inputEvent.getContextId(), lockDuration, lockRetries, lockRetryMaxDuration);
                contextRepository.getContext(inputEvent.getContextId());
            }

            // get or determine current step
            if (StringUtils.isEmpty(context.getCurrentStep())) {
                context.setCurrentStep(flow.getDefaultStep());
                logger.debug(context.getId() + " state is empty, using default defaultStep " + flow.getDefaultStep());
            }

            CdslRuntime runtime = new CdslRuntime();
            runtime.setAuditor(auditor);

            // get the step
            FlowStep step = null;
            FlowStep nextStep = flowRegistry.getFlowStep(context.getCurrentStep());
            CdslOutputEvent output = null;

            while (nextStep != null) {
                context.setCurrentStep(nextStep.getId());
                runtime.getAuditor().transition(context, flow.getId(), nextStep.getId());

                step = nextStep;
                nextStep = null;
                String logPrefix = step.getId();

                logger.debug("Executing " + step.getId());
                try {
                    CdslOutputEvent result;
                    CdslOutputEvent generalOutput = obtainOutputs(runtime, context, inputEvent, flow, step, step.getLogicElements());
                    CdslOutputEvent finalOutput = obtainOutputs(runtime, context, inputEvent, flow ,step, step.getFinalElements());

                    if (finalOutput != null) {
                        logger.debug("Result of  " + step.getId() + " provided by final group DSL group");
                        result = finalOutput;
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
                            auditor.executePostStep(context,flow.getId(),step.getId(), postStepTask);
                            postStepTask.runTask();
                        } catch (Exception ex) {
                            runtime.getAuditor().error(context, flow.getId(), step.getId(), null, ex);
                            logger.debug(logPrefix + " caught exception in post step task - ignoring " + ex.getMessage(), ex);
                        }
                    }
                    runtime.getPostStepTasks().clear();

                    if (CdslOutputEvent.Action.Route.equals(result.getAction())) {
                        logger.debug(logPrefix + " routing to " + result.getNextRoute());
                        context.setCurrentStep(result.getNextRoute());
                        nextStep = flowRegistry.getFlowStep(result.getNextRoute());
                    } else if (CdslOutputEvent.Action.Await.equals(result.getAction())) {
                        logger.debug(logPrefix + " awaiting at " + result.getNextRoute());
                        context.setCurrentStep(result.getNextRoute());
                    } else if (CdslOutputEvent.Action.End.equals(result.getAction())) {
                        logger.debug(logPrefix + " end at " + result.getNextRoute());
                    } else if (CdslOutputEvent.Action.Reject.equals(result.getAction())) {
                        logger.debug(logPrefix + " rejected" + result.getNextRoute());
                    }
                    output = result;

                } catch (Exception ex) {
                    logger.warn("Exception Caught " + ex.getMessage() + " - routing to exception handling step " + flow.getErrorStep(), ex);
                    runtime.getAuditor().error(context, flow.getId(), step.getId(), null, ex);
                    nextStep = flowRegistry.getFlowStep(flow.getErrorStep());
                }

            }

            // save context
            contextRepository.saveContext(context);

            // release lock
            lockProvider.release(lock);
            lock = null;

            // execute post txn tasks
            for (PostCommitTask postCommitTask : runtime.getPostCommitTasks()) {
                try {
                    auditor.executePostCommit(context,flow.getId(),postCommitTask);
                    postCommitTask.runTask();
                } catch (Exception ex) {
                    logger.debug(flow.getId() + " caught exception in post commit task - ignoring " + ex.getMessage(), ex);
                }
            }
            runtime.getPostCommitTasks().clear();

            // output something
            if (output == null) {
                output = new CdslOutputEvent();
            }
            output.setContextId(context.getId());
            output.setContextState(context.getState());
            return output;

        } catch (LockRejectedException lockRejected) {
            logger.warn("Lock Rejected ", lockRejected);
        } finally {
            if (lock != null)
                lockProvider.release(lock);
        }

        return null;
    }

}
