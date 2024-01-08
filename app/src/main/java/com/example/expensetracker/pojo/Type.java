package com.example.expensetracker.pojo;

public class Type extends SettingsParent {
    String name;

    public Type(String name) {
        this.name = name;
    }

    @Override
    public SettingsType getType() {
        return SettingsType.TYPE;
    }

    @Override
    public String getName() {
        return name;
    }
}
