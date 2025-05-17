package com.example.expensetracker;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private int currentNav;
    private Settings settings;
    private long pressedTime;
    public static final SimpleDateFormat userFriendlyDateFormatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss a", Locale.ENGLISH);
    private ImageView deleteBtn;
    private ImageView editBtn;

    public ImageView getDeleteButton() {
        return deleteBtn;
    }
    public ImageView getEditButton() {
        return editBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBManager.createDBManagerInstance(this);
        settings = Settings.createWithParametersFromDatabase();

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        long lastTime = sharedPref.getLong("LastTime", 0);
        SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();

        boolean isFirstOpen = !sharedPref.getBoolean("NotFirstOpen", false);

        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        deleteBtn = findViewById(R.id.delete_entry_button);
        editBtn = findViewById(R.id.edit_entry_button);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if(isFirstOpen) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage("The App has a feature that can allow it go through your SMS and create entries, saving you the hassle of doing so manually. To enable this feature the App requires SMS read permission.")
                        .setCancelable(false)
                        .setPositiveButton("Ok, Got it", (dialog, which) ->
                        {
                            sharedPrefEditor.putBoolean("NotFirstOpen", true);
                            sharedPrefEditor.apply();
                            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_SMS}, 1);
                        })
                        .show();
            }
        } else {
            SmsReader reader = new SmsReader();
            Date lastDate = new Date(lastTime);
            sharedPrefEditor.putLong("LastTime", new Date().getTime());
            sharedPrefEditor.apply();
            ArrayList<UnconfirmedEntry> entries = reader.readMessagesSentAfter(this, lastDate);
            entries.forEach(entry -> DBManager.getDBManagerInstance().insertUnconfirmedEntries(entry));
        }

        NavController mNavigationController = Navigation.findNavController(this,R.id.fragment_container_view);
        NavigationUI.setupActionBarWithNavController(this, mNavigationController);

        mNavigationController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            currentNav = navDestination.getId();
            invalidateOptionsMenu();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (pressedTime + 2000 > System.currentTimeMillis()) {
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                }
                pressedTime = System.currentTimeMillis();
            }
        };
        getOnBackPressedDispatcher().addCallback(callback);

        currentNav = R.id.homeFragment;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && permissions[0].equals(android.Manifest.permission.READ_SMS) && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("You have denied to use the SMS Read Feature of the App. If you would like to turn it on in the future, go to settings and give SMS Permission to the App. On restarting the app SMS reading should be enabled.")
                    .setCancelable(true)
                    .setPositiveButton("Ok, Got it!", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_action_settings).setVisible(currentNav != R.id.settingsFragment);
        menu.findItem(R.id.menu_unconfirmed_entries).setVisible(currentNav != R.id.unconfirmedEntryFragment);
        menu.findItem(R.id.menu_users_split).setVisible(currentNav != R.id.userSplitFragment);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(R.id.menu_action_settings == item.getItemId()) {
            deleteBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
            invalidateMenu();
            NavController navController = Navigation.findNavController(this, R.id.fragment_container_view);
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.homeFragment) {
                navController.navigateUp();
            }
            navController.navigate(R.id.action_homeFragment_to_settingsFragment);
            return true;
        } else if (R.id.menu_unconfirmed_entries == item.getItemId()) {
            deleteBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
            invalidateMenu();
            NavController navController = Navigation.findNavController(this, R.id.fragment_container_view);
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.homeFragment) {
                navController.navigateUp();
            }
            navController.navigate(R.id.action_homeFragment_to_unconfirmedEntryFragment);
            return true;
        } else if (R.id.menu_users_split == item.getItemId()) {
            deleteBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
            invalidateMenu();
            NavController navController = Navigation.findNavController(this, R.id.fragment_container_view);
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.homeFragment) {
                navController.navigateUp();
            }
            navController.navigate(R.id.action_homeFragment_to_userSplitFragment);
            return true;
        } else if (R.id.menu_export_data == item.getItemId()) {
            deleteBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
            invalidateMenu();
            NavController navController = Navigation.findNavController(this, R.id.fragment_container_view);
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() != R.id.homeFragment) {
                navController.navigateUp();
            }
            navController.navigate(R.id.action_homeFragment_to_exportFragment);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this,R.id.fragment_container_view).navigateUp();
    }

    public Settings getSettings() {
        return settings;
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