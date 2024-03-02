package com.example.expensetracker.pojo;

import android.util.JsonReader;
import android.util.JsonWriter;

import com.example.expensetracker.R;

import java.io.IOException;
import java.util.Objects;

public class SubCategory extends SettingsParent {
    private long id;
    String name;
    int drawableId;
    long categoryId;

    public SubCategory(long id, String name, long categoryId, int drawableId) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.drawableId = drawableId;
    }

    public SubCategory(String name, int categoryId) {
        this(-1, name, categoryId, R.drawable.ic_launcher_background);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategoryId(long categoryId) { this.categoryId = categoryId;}

    public long getId() {
        return id;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.SUBCATEGORY;
    }
}
