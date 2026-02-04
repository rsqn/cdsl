package tech.rsqn.cdsl.dsl;

/**
 * Model for the "if" container DSL.
 * <code>condition</code> can be:
 * <ul>
 *   <li>Literal: <code>true</code> or <code>false</code> (case-insensitive)</li>
 *   <li>Context var exists: <code>varName</code> – true when ctx.getVar(varName) is non-null and non-empty</li>
 *   <li>Context var equals: <code>varName=value</code> – true when ctx.getVar(varName) equals value</li>
 *   <li>Context var not equals: <code>varName!=value</code> – true when ctx.getVar(varName) does not equal value</li>
 * </ul>
 */
public class IfModel {
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
