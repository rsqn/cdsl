package tech.rsqn.cdsl.dsl;

/**
 * Model for the "foreach" container DSL.
 * Iterates over a list stored in context as a string (e.g. comma-separated).
 * - listVar: context variable name containing the list string.
 * - itemVar: context variable name to set with the current item each iteration.
 * - separator: optional, default ",". Delimiter used to split listVar value.
 */
public class ForEachModel {
    private String listVar;
    private String itemVar;
    private String separator = ",";

    public String getListVar() {
        return listVar;
    }

    public void setListVar(String listVar) {
        this.listVar = listVar;
    }

    public String getItemVar() {
        return itemVar;
    }

    public void setItemVar(String itemVar) {
        this.itemVar = itemVar;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator != null ? separator : ",";
    }
}
