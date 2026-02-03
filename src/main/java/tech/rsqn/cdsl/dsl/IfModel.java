package tech.rsqn.cdsl.dsl;

/**
 * Model for the "if" container DSL.
 * Condition: when "var" is set, run nested elements only if the context variable matches.
 * - If both var and val are set: run when context.getVar(var) equals val.
 * - If only var is set: run when context.getVar(var) is not null and not empty.
 */
public class IfModel {
    private String var;
    private String val;

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
