package com.example.expensetracker.pojo;

import java.util.Date;

public class UnconfirmedEntry {
    private final String sender;
    private final String body;
    private final Date sentDate;
    private final int value;
    private final boolean isCredited;

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

    public String getBody() {
        return body;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public int getValue() {
        return value;
    }

    public boolean isCredited() {
        return isCredited;
    }
}
