package com.example.expensetracker.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Entry {
    public  static class SharedUser {
        private User user;
        private int value;

        public SharedUser(User user, int value) {
            this.user = user;
            this.value = value;
        }
        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
    private String name;
    private int value;
    private int categoryId;
    private int subCategoryId;
    private int paymentId;
    private Date date;
    private ArrayList<SharedUser> sharedUsersList;

    public Entry(String name, int value, int categoryId, int subCategoryId, int paymentId, Date date, Date time) {
        this.name = name;
        this.value = value;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.paymentId = paymentId;
        this.date = new Date(date.getTime() + time.getTime());
        this.sharedUsersList = new ArrayList<>();
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

    public ArrayList<SharedUser> getSharedUsersList() {
        return sharedUsersList;
    }

    public void setSharedUsersList(ArrayList<SharedUser> sharedUsersList) {
        this.sharedUsersList = sharedUsersList;
    }

    public boolean getIsShared() {
        return sharedUsersList.isEmpty();
    }
}
