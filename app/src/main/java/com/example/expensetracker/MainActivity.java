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

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

/**
 * -1 : File Write Error
*/

public class MainActivity extends AppCompatActivity {
    private boolean isSettingsVisible;
    private ExpenseSettings expenseSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        File f = new File(getFilesDir(), getString(R.string.settings_filename));
        if(!f.exists()) {
            expenseSettings = ExpenseSettings.createWithDefaultParameters(this);
            try {
                expenseSettings.writeSettingsToJson(f);
            } catch(IOException e) {
                terminateApplicationWithError(-1);
            }
            Snackbar.make(findViewById(android.R.id.content), "Settings not found opening with default settings", Snackbar.LENGTH_LONG).show();
        } else {
            try {
                expenseSettings = ExpenseSettings.createWithParametersFromFile(this, f);
            } catch(IOException e) {
                if(!f.delete()) {
                    Log.d("MyTag", "This is Impossible");
                }
                expenseSettings = ExpenseSettings.createWithDefaultParameters(this);
                try {
                    expenseSettings.writeSettingsToJson(f);
                    Snackbar.make(findViewById(android.R.id.content), "Error: Couldn't read settings file. Initializing with default settings", Snackbar.LENGTH_LONG).show();
                } catch (IOException ex) {
                    terminateApplicationWithError(-1);
                }
            }
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

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 1);
        } else {
            SmsReader reader = new SmsReader();
            reader.readMessagesSentAfter(this, null);
        }
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

    public ExpenseSettings getSettings() {
        return expenseSettings;
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