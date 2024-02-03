package com.example.expensetracker.pojo;

import com.example.expensetracker.R;

public class User extends SettingsParent {
    private int id;
     private String name;
     private int drawableId;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
        this.drawableId = R.drawable.ic_launcher_background;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.USER;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(int drawableId) {
        this.drawableId = drawableId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
