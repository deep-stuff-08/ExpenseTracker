package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private boolean isSettingsVisible;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        NavController mNavigationController = Navigation.findNavController(this,R.id.fragment_container_view);
        NavigationUI.setupActionBarWithNavController(this, mNavigationController);

        mNavigationController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if(navDestination.getId() == R.id.homeFragment) {
                isSettingsVisible = true;
            } else {
                isSettingsVisible = false;
            }
            invalidateOptionsMenu();
        });

        isSettingsVisible = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_action_settings).setVisible(isSettingsVisible);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.menu_action_settings == item.getItemId()) {
            invalidateMenu();
            Navigation.findNavController(this,R.id.fragment_container_view).navigate(R.id.action_homeFragment_to_settingsFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this,R.id.fragment_container_view).navigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}