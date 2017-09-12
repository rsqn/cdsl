package tech.rsqn.cdsl.model;

import java.util.Date;

public class CdslAuditEvent {
    private Date ts;
    private String source;
    private String category;
    private String attr;
    private String from;
    private String to;

    public CdslAuditEvent() {
        ts = new Date();
    }

    public CdslAuditEvent with(String source, String c) {
        this.source = source;
        this.category = c;
        return this;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "CdslAuditEvent{" +
                "ts=" + ts +
                ", source='" + source + '\'' +
                ", category='" + category + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
