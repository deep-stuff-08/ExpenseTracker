package com.example.expensetracker.pojo;

import java.util.Date;

public class UnconfirmedEntry {

    private long id;
    private final String sender;
    private final String body;
    private final Date sentDate;
    private final float value;

    public UnconfirmedEntry(String sender, String body, Date sentDate, float value) {
        this.sender = sender;
        this.body = body;
        this.sentDate = sentDate;
        this.value = value;
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

    public float getValue() {
        return value;
    }

    public boolean isExpense() {
        return value < 0;
    }

    public boolean isIncome() {
        return !isExpense();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
