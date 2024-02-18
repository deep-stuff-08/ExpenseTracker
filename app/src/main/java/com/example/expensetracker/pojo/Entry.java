package com.example.expensetracker.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Entry {
    public  static class SharedUser {
        private long id;
        private User user;
        private int value;

        public SharedUser(User user, int value) {
            this.user = user;
            this.value = value;
        }

        public SharedUser(User user, long id, int value) {
            this.user = user;
            this.id = id;
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
    private long categoryId;
    private long subCategoryId;
    private long paymentId;
    private Date dateAndTime;
    private long id;
    private ArrayList<SharedUser> sharedUsersList;

	public Entry(long id, String name, int value, long categoryId, long subCategoryId, long paymentId, Date date, Date time, ArrayList<SharedUser> sharedUsersList) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.paymentId = paymentId;
        this.dateAndTime = new Date(date.getTime() + time.getTime());
        this.sharedUsersList = sharedUsersList;
    }
    public Entry(String name, int value, long categoryId, long subCategoryId, long paymentId, Date date, Date time, ArrayList<SharedUser> sharedUsersList) {
        this(-1, name, value, categoryId, subCategoryId, paymentId, date, time, sharedUsersList);
    }

    public Entry(long id, String name, int value, long categoryId, long subCategoryId, long paymentId, Date date, Date time) {
        this(id, name, value, categoryId, subCategoryId, paymentId, date, time, new ArrayList<>());
    }

    public Entry(String name, int value, long categoryId, long subCategoryId, long paymentId, Date date, Date time) {
        this(-1, name, value, categoryId, subCategoryId, paymentId, date, time, new ArrayList<>());
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

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(Date date, Date time) {
        this.dateAndTime = new Date(date.getTime() + time.getTime());
    }

    public ArrayList<SharedUser> getSharedUsersList() {
        return sharedUsersList;
    }

    public void setSharedUsersList(ArrayList<SharedUser> sharedUsersList) {
        this.sharedUsersList = sharedUsersList;
    }

    public boolean isShared() {
        return sharedUsersList.isEmpty();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
