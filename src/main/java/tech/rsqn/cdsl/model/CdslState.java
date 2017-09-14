package tech.rsqn.cdsl.model;

public class CdslState {
    public enum State {Alive, Await, End, Error}

    private State state;
    private String errCode;
    private String errMsg;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
