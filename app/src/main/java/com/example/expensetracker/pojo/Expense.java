package com.example.expensetracker.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Expense {
    public  static class SharedUser {
        private String name;
        private int value;

        public SharedUser(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public SharedUser() {
            this.name = "";
            this.value = 0;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
    private String name;
    private int value;
    private boolean isDebit;
    private int categoryId;
    private int subCategoryId;
    private int paymentId;
    private Date date;
    private Date time;
    private ArrayList<SharedUser> sharedUsersList;

    public Expense(String name, int value, boolean isDebit, int categoryId, int subCategoryId, int paymentId, Date date, Date time, ArrayList<SharedUser> sharedUsersList) {
        this.name = name;
        this.value = value;
        this.isDebit = isDebit;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.paymentId = paymentId;
        this.date = date;
        this.time = time;
        this.sharedUsersList = sharedUsersList;
    }
}
