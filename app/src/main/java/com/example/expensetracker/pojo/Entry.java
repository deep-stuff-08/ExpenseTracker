package com.example.expensetracker.pojo;

import java.util.ArrayList;
import java.util.Date;

public class Entry {
    public  static class SharedUser {
        private final User user;
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
    }
    private String name;
    private int value;
    final private long categoryId;
    final private long subCategoryId;
    final private long paymentId;
    final private Date dateAndTime;
    private long id;
    private ArrayList<SharedUser> sharedUsersList;
    private User payer;

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

    public long getSubCategoryId() {
        return subCategoryId;
    }
    public long getCategoryId() {
        return categoryId;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public Date getDateAndTime() {
        return dateAndTime;
    }

    public ArrayList<SharedUser> getSharedUsersList() {
        return sharedUsersList;
    }

    public User getPayer() {
        return payer;
    }

    public void setSharedUsersList(User payer, ArrayList<SharedUser> sharedUsersList) {
        this.payer = payer;
        this.sharedUsersList = sharedUsersList;
    }

    public boolean isShared() {
        return !sharedUsersList.isEmpty();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
