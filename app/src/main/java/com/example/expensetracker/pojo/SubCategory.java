package com.example.expensetracker.pojo;

import com.example.expensetracker.R;

public class SubCategory extends SettingsParent {
    private final long id;
    String name;
    int drawableId;

    public SubCategory(long id, String name, int drawableId) {
        this.id = id;
        this.name = name;
        this.drawableId = drawableId;
    }

    public SubCategory(String name, int drawableId) {
        this(-1, name, drawableId);
    }

    public SubCategory(String name) {
        this(-1, name, R.drawable.ic_launcher_background);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
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
