package tech.rsqn.cdsl.dsl;

/**
 * Model for the "if" container DSL.
 * <code>condition</code> can be:
 * <ul>
 *   <li>Literal: <code>true</code> or <code>false</code> (case-insensitive)</li>
 *   <li>Context var exists: <code>varName</code> – true when ctx.getVar(varName) is non-null and non-empty</li>
 *   <li>Context var equals: <code>varName=value</code> or <code>varName == value</code></li>
 *   <li>Context var not equals: <code>varName!=value</code></li>
 *   <li>OR: <code>expr1 || expr2</code> – true when either sub-expression is true</li>
 *   <li>Single-quoted values are supported: <code>varName == 'CRISIS'</code> strips the quotes before comparing</li>
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
