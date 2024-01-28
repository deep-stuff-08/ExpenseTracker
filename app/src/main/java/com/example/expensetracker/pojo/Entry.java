package com.example.expensetracker.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Entry {
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

    public Entry(String name, int value, boolean isDebit, int categoryId, int subCategoryId, int paymentId, Date date, Date time, ArrayList<SharedUser> sharedUsersList) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isDebit() {
        return isDebit;
    }

    public void setDebit(boolean debit) {
        isDebit = debit;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public ArrayList<SharedUser> getSharedUsersList() {
        return sharedUsersList;
    }

    public void setSharedUsersList(ArrayList<SharedUser> sharedUsersList) {
        this.sharedUsersList = sharedUsersList;
    }
}
