package com.example.expensetracker.pojo;

import com.example.expensetracker.R;

import java.util.ArrayList;

public class Category extends SettingsParent {
    private String name;
    private  long id;
    private int colorId;
    private ArrayList<SubCategory> subCategories;

    public Category(int id, String name, int colorId, ArrayList<SubCategory> subCategories) {
        this.id = id;
        this.name = name;
        this.colorId = colorId;
        this.subCategories = subCategories;
    }

    public Category(String name) {
        this(-1, name, R.color.categoryGreen, new ArrayList<>());
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.CATEGORY;
    }
}
