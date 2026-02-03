package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.dsl.DslMetadata;
import tech.rsqn.cdsl.execution.Flow;
import tech.rsqn.cdsl.execution.FlowStep;
import tech.rsqn.cdsl.execution.NestedElementExecutor;
import tech.rsqn.cdsl.execution.PostCommitTask;
import tech.rsqn.cdsl.execution.PostStepTask;
import tech.rsqn.cdsl.model.CdslOutputValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CdslRuntime {
    private String transactionId;
    private CdslContextAuditor auditor;
    private List<PostStepTask> postStepTasks;
    private List<PostCommitTask> postCommitTasks;
    private Map<String,CdslOutputValue> outputValueMap;
    private NestedElementExecutor nestedElementExecutor;
    private DslMetadata currentElementMetadata;
    private Flow currentFlow;
    private FlowStep currentStep;

    public CdslRuntime() {
        postStepTasks = new ArrayList<>();
        postCommitTasks = new ArrayList<>();
        outputValueMap = new HashMap<>();
    }

    public void emit(CdslOutputValue v) {
        outputValueMap.put(v.getName(),v);
    }

    public Map<String, CdslOutputValue> getOutputValueMap() {
        return outputValueMap;
    }

    public CdslOutputValue getOutputValue(String n) {
        return outputValueMap.get(n);
    }

    public void postStep(PostStepTask task) {
        postStepTasks.add(task);
    }

    public void postCommit(PostCommitTask task) {
        postCommitTasks.add(task);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public CdslContextAuditor getAuditor() {
        return auditor;
    }

    public void setAuditor(CdslContextAuditor auditor) {
        this.auditor = auditor;
    }

    public List<PostStepTask> getPostStepTasks() {
        return postStepTasks;
    }

    public void setPostStepTasks(List<PostStepTask> postStepTasks) {
        this.postStepTasks = postStepTasks;
    }

    public List<PostCommitTask> getPostCommitTasks() {
        return postCommitTasks;
    }

    public void setPostCommitTasks(List<PostCommitTask> postCommitTasks) {
        this.postCommitTasks = postCommitTasks;
    }

    public NestedElementExecutor getNestedElementExecutor() {
        return nestedElementExecutor;
    }

    public void setNestedElementExecutor(NestedElementExecutor nestedElementExecutor) {
        this.nestedElementExecutor = nestedElementExecutor;
    }

    public DslMetadata getCurrentElementMetadata() {
        return currentElementMetadata;
    }

    public void setCurrentElementMetadata(DslMetadata currentElementMetadata) {
        this.currentElementMetadata = currentElementMetadata;
    }

    public Flow getCurrentFlow() {
        return currentFlow;
    }

    public void setCurrentFlow(Flow currentFlow) {
        this.currentFlow = currentFlow;
    }

    public FlowStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(FlowStep currentStep) {
        this.currentStep = currentStep;
    }
}
