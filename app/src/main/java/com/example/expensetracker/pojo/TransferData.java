package com.example.expensetracker.pojo;

public class TransferData {
    private long userId;
    private int value;

    public TransferData(long userId, int value) {
        this.userId = userId;
        this.value = value;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
