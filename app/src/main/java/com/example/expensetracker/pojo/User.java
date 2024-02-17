package com.example.expensetracker.pojo;

import com.example.expensetracker.R;

public class User extends SettingsParent {
    private long id;
    private String name;
    private int drawableId;

    public User(long id, String name) {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
