package tech.rsqn.cdsl.dsl;

public class ComplexModel {

    private String aModelProperty;
    private ComplexModelChild modelChild;

    public String getaModelProperty() {
        return aModelProperty;
    }

    public void setaModelProperty(String aModelProperty) {
        this.aModelProperty = aModelProperty;
    }

    public ComplexModelChild getModelChild() {
        return modelChild;
    }

    public void setModelChild(ComplexModelChild modelChild) {
        this.modelChild = modelChild;
    }
}
