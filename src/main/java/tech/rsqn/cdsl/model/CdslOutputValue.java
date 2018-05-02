package tech.rsqn.cdsl.model;

public class CdslOutputValue {
    private String name;
    private Object val;

    public CdslOutputValue withName(String n) {
        this.name = n;
        return this;
    }

    public CdslOutputValue andValue(Object v) {
        this.val = v;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public <T> T getVal() {
        return (T) val;
    }

    public void setVal(Object val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "CdslOutputValue{" +
                "name='" + name + '\'' +
                ", val=" + val +
                '}';
    }
}
