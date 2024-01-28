package com.example.expensetracker.pojo;

import java.util.ArrayList;

public class Category extends SettingsParent {
    private String name;

    private  int id;
    private int colorId;
    final private ArrayList<SubCategory> subCategories;

    public Category(int id, String name, int colorId, ArrayList<SubCategory> subCategories) {
        this.id = id;
        this.name = name;
        this.colorId = colorId;
        this.subCategories = subCategories;
    }

    public Category() {
        this.name = "";
        this.colorId = 0;
        this.subCategories = new ArrayList<>();
    }

    public int getId() {
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

    public ArrayList<SubCategory> getExpenseSubCategories() {
        return subCategories;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.CATEGORY;
    }
}
