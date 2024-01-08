package com.example.expensetracker;

public interface SettingsUpdateListener {
    void onSettingsUpdateListener(int position, String newName, int newLogo);
    void onSettingsDeleteListener(int position);
    void onSettingsAddListener(String name, int logo);
}
