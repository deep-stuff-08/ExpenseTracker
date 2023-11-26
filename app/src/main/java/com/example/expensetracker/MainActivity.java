package com.example.expensetracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

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
            expenseSettings = ExpenseSettings.createWithDefaultParameters();
            try {
                expenseSettings.writeSettingsToJson(f);
            } catch(IOException e) {
                terminateApplicationWithError(-1);
            }
            Snackbar.make(findViewById(android.R.id.content), "Settings not found opening with default settings", Snackbar.LENGTH_LONG).show();
        } else {
            try {
                expenseSettings = ExpenseSettings.createWithParametersFromFile(f);
            } catch(IOException e) {
                f.delete();
                expenseSettings = ExpenseSettings.createWithDefaultParameters();
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

    public ExpenseSettings getSettings() {
        return expenseSettings;
    }

    public void terminateApplicationWithError(int errorCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Unrecoverable error, contact Developer. Exiting Now");
        builder.setTitle("Error Code " + errorCode);
        builder.setCancelable(false);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.create().show();
    }
}