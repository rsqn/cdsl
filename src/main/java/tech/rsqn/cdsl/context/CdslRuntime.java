package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.execution.PostCommitTask;
import tech.rsqn.cdsl.execution.PostStepTask;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.ArrayList;
import java.util.List;

public class CdslRuntime {
    private CdslContextAuditor auditor;
    private List<PostStepTask> postStepTasks;
    private List<PostCommitTask> postCommitTasks;
    private List<CdslOutputEvent> outputs;

    public CdslRuntime() {
        postStepTasks = new ArrayList<>();
        postCommitTasks = new ArrayList<>();
        outputs = new ArrayList<>();
    }

    public void appendOutput(CdslOutputEvent eventWithOutput) {
        outputs.add(eventWithOutput);
    }

    public List<CdslOutputEvent> getOutputs() {
        return outputs;
    }

    public CdslOutputEvent getLatestOutput(CdslOutputEvent ev) {
        if (outputs.size() > 0) {
            return outputs.get(outputs.size() - 1);
        }
        return null;
    }

    public void setOutputs(List<CdslOutputEvent> outputs) {
        this.outputs = outputs;
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
}
