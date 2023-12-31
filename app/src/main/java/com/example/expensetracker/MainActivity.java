package com.example.expensetracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * -1 : File Write Error
*/

public class MainActivity extends AppCompatActivity {
    private boolean isSettingsVisible;
    private ExpenseSettings expenseSettings;

    public DBManager dbManager;
    private ArrayList<UnconfirmedEntry> entries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        expenseSettings = ExpenseSettings.readDataFromDatabase(this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 1);
        } else {
            SmsReader reader = new SmsReader();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, 2023);
            c.set(Calendar.MONTH, 11);
            c.set(Calendar.DAY_OF_MONTH, 27);
            c.set(Calendar.HOUR_OF_DAY, 9);
            c.set(Calendar.MINUTE, 40);
            c.set(Calendar.SECOND, 30);
            entries = reader.readMessagesSentAfter(this, c.getTime());
        }
        NavController mNavigationController = Navigation.findNavController(this,R.id.fragment_container_view);
        NavigationUI.setupActionBarWithNavController(this, mNavigationController);

        mNavigationController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            isSettingsVisible = navDestination.getId() == R.id.homeFragment;
            invalidateOptionsMenu();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);

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
            Navigation.findNavController(this, R.id.fragment_container_view).navigate(R.id.action_homeFragment_to_settingsFragment);
            return true;
        } else if (R.id.menu_unconfirmed_entries == item.getItemId()) {
            invalidateMenu();
            NavController navController = Navigation.findNavController(this, R.id.fragment_container_view);
            if(navController.getCurrentDestination().getId() != R.id.homeFragment) {
                navController.navigateUp();
            }
            navController.navigate(R.id.action_homeFragment_to_unconfirmedEntryFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this,R.id.fragment_container_view).navigateUp();
    }

    public ExpenseSettings getSettings() {
        return expenseSettings;
    }

    public ArrayList<UnconfirmedEntry> getEntries() {
        return entries;
    }

    public void terminateApplicationWithError(int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unrecoverable error, contact Developer. Exiting Now");
        builder.setTitle("Error Code " + errorCode);
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", (dialogInterface,  i) -> finish());
        builder.create().show();
    }
}