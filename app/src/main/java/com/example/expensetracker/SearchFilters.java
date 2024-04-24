package com.example.expensetracker;

import android.widget.Filter;

import com.example.expensetracker.database.DBManager;

import java.util.ArrayList;
import java.util.Date;

public class SearchFilters {
    int type;
    Date dateAfter;
    Date dateBefore;
    ArrayList<Long> paymentType;
    ArrayList<Long> category;

    public int getType() {
        return type;
    }

    public Date getDateAfter() {
        return dateAfter;
    }

    public Date getDateBefore() {
        return dateBefore;
    }

    public ArrayList<Long> getPaymentType() {
        return paymentType;
    }

    public ArrayList<Long> getCategory() {
        return category;
    }

    public SearchFilters() {
        type = 0;
        dateAfter = null;
        dateBefore = null;
        paymentType = null;
        category = null;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDate(Date dateBefore, Date dateAfter) {
        this.dateAfter = dateAfter;
        this.dateBefore = dateBefore;
    }

    public void setPaymentType(ArrayList<Long> paymentType) {
        this.paymentType = paymentType;
    }

    public void setCategory(ArrayList<Long> category) {
        this.category = category;
    }
}
