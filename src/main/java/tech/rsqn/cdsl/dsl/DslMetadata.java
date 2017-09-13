package tech.rsqn.cdsl.dsl;

public class DslMetadata<T> {
    public enum ResolutionStrategy {ByType, ByName}
    private String name;
    private Class cls;
    private Class modelCls;
    private T model;
    private ResolutionStrategy resolutionStrategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public Class getModelCls() {
        return modelCls;
    }

    public void setModelCls(Class modelCls) {
        this.modelCls = modelCls;
    }

    public ResolutionStrategy getResolutionStrategy() {
        return resolutionStrategy;
    }

    public void setResolutionStrategy(ResolutionStrategy resolutionStrategy) {
        this.resolutionStrategy = resolutionStrategy;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
