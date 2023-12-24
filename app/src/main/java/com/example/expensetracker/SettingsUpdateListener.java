package com.example.expensetracker;

public interface SettingsUpdateListener {
    void onSettingsNameUpdateListener(int position, String newName);
    void onSettingsLogoUpdateListener(int position, int newLogo);
    void onSettingsDeleteListener(int position);
    void onSettingsAddListener(String name, int logo);
}
