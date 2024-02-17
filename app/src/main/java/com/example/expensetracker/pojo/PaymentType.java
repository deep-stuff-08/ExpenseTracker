package com.example.expensetracker.pojo;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public class PaymentType extends SettingsParent {
    private long id;
    private String name;
    private int drawableId;

    public PaymentType() {
        name = "";
        drawableId = 0;
    }

    public PaymentType(int id, String name, int drawableId) {
        this.id = id;
        this.name = name;
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.PAYMENT;
    }
}
