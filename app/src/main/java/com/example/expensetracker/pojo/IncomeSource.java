package com.example.expensetracker.pojo;

public class IncomeSource extends SettingsParent {

    String name;

    public IncomeSource(String name) {
        this.name = name;
    }
    @Override
    public SettingsType getType() {
        return SettingsType.INCOME;
    }

    @Override
    public String getName() {
        return name;
    }
}
