package tech.rsqn.cdsl.dsl;

import java.util.ArrayList;
import java.util.List;

public class DslMetadata<T> {
    public enum ResolutionStrategy {ByType, ByName}
    private String name;
    private Class cls;
    private Class modelCls;
    private T model;
    private ResolutionStrategy resolutionStrategy;
    private List<DslMetadata> childElements;

    public List<DslMetadata> getChildElements() {
        return childElements;
    }

    public void setChildElements(List<DslMetadata> childElements) {
        this.childElements = childElements;
    }

    public void addChildElement(DslMetadata child) {
        if (this.childElements == null) {
            this.childElements = new ArrayList<>();
        }
        this.childElements.add(child);
    }

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
