package tech.rsqn.cdsl.model;

import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;

public class CdslFlowOutputEvent extends CdslOutputEvent {
    private Map<String,CdslOutputValue> outputValues;

    public CdslFlowOutputEvent() {
        outputValues = new HashMap<>();
    }


    public CdslFlowOutputEvent with(CdslOutputEvent ev) {
        BeanUtils.copyProperties(ev,this);
        return this;
    }

    public CdslOutputValue getOutputValue(String n) {
        return outputValues.get(n);
    }

    public Map<String, CdslOutputValue> getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(Map<String, CdslOutputValue> outputs) {
        this.outputValues = outputs;
    }



    @Override
    public String toString() {
        return super.toString() + " : " + " CdslFlowOutputEvent{" +
                "outputValues=" + outputValues +
                '}';
    }
}
