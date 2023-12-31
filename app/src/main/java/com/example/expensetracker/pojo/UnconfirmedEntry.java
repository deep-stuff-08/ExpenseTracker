package com.example.expensetracker.pojo;

import java.util.Date;

public class UnconfirmedEntry {
    private String sender;
    private String body;
    private Date sentDate;
    private int value;
    private boolean isCredited;

    public UnconfirmedEntry() {
    }

    public UnconfirmedEntry(String sender, String body, Date sentDate, int value, boolean isCredited) {
        this.sender = sender;
        this.body = body;
        this.sentDate = sentDate;
        this.value = value;
        this.isCredited = isCredited;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isCredited() {
        return isCredited;
    }

    public void setCredited(boolean credited) {
        isCredited = credited;
    }
}
