package tech.rsqn.cdsl.model;

import java.io.Serializable;

public class CdslInputEvent<T extends Serializable> {
    private String contextId;
    private String name;
    private String source;
    private T model;

    public CdslInputEvent with(String source, String name) {
        this.name = name;
        this.source = source;
        return this;
    }

    public CdslInputEvent andContextId(String contextId) {
        this.contextId = contextId;
        return this;
    }

    public CdslInputEvent andModel(T model) {
        this.model = model;
        return this;
    }

    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "CdslInputEvent{" +
                "contextId='" + contextId + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", model=" + model +
                '}';
    }
}
