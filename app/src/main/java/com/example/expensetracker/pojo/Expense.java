package com.example.expensetracker.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Expense {
    String name;
    int value;
    boolean isDebit;
    int categoryId;
    int subCategoryId;
    int paymentId;
    Date date;
    Date time;
    boolean isShared;

    public Expense(String name, int value, boolean isDebit, int categoryId, int subCategoryId, int paymentId, Date date, Date time, boolean isShared) {
        this.name = name;
        this.value = value;
        this.isDebit = isDebit;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.paymentId = paymentId;
        this.date = date;
        this.time = time;
        this.isShared = isShared;
    }
}
