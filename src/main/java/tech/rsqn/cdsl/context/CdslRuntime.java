package tech.rsqn.cdsl.context;

import tech.rsqn.cdsl.execution.PostCommitTask;
import tech.rsqn.cdsl.execution.PostStepTask;
import tech.rsqn.cdsl.model.CdslOutputEvent;

import java.util.ArrayList;
import java.util.List;

public class CdslRuntime {
    private String transactionId;
    private CdslContextAuditor auditor;
    private List<PostStepTask> postStepTasks;
    private List<PostCommitTask> postCommitTasks;

    public CdslRuntime() {
        postStepTasks = new ArrayList<>();
        postCommitTasks = new ArrayList<>();
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
}
