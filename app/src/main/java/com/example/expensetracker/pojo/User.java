package com.example.expensetracker.pojo;

import com.example.expensetracker.R;

public class User extends SettingsParent {
    String name;
    int logo;

    public User(String name) {
        this.name = name;
        this.logo = R.drawable.ic_launcher_background;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.USER;
    }

    @Override
    public String getName() {
        return name;
    }
}
