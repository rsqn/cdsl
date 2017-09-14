package tech.rsqn.cdsl.execution;

@FunctionalInterface
public interface PostCommitTask {
    void runTask();
}
