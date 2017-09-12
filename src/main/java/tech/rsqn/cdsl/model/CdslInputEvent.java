package tech.rsqn.cdsl.model;

public class CdslInputEvent {
    private String name;
    private String source;

    public CdslInputEvent with(String source, String name) {
        this.name = name;
        this.source = source;
        return this;
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
}
