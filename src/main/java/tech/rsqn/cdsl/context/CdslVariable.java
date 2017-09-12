package tech.rsqn.cdsl.context;

public class CdslVariable {
    private String v;

    public CdslVariable with(String v) {
        this.v = v;
        return this;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "{" +
                "v='" + v + '\'' +
                '}';
    }
}
