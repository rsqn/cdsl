package tech.rsqn.cdsl.execution;

import java.io.Serializable;

public class CustomInputModel implements Serializable {
    private String theMessage;

    public String getTheMessage() {
        return theMessage;
    }

    public void setTheMessage(String theMessage) {
        this.theMessage = theMessage;
    }
}
