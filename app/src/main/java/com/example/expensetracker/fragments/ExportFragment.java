package com.example.expensetracker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.SearchFilters;
import com.example.expensetracker.Settings;
import com.example.expensetracker.adapters.ExportEntryAdapter;
import com.example.expensetracker.adapters.SettingsEntryAdapter;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.database.DatabaseDetails;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.Entry;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.google.android.material.snackbar.Snackbar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExportFragment extends Fragment {
    public ExportFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void activityCallback(View view, ActivityResult result, int source) {
        if(result.getResultCode() == Activity.RESULT_CANCELED) { Snackbar.make(view, "Export Canceled", Snackbar.LENGTH_SHORT).show(); return; }
        if(result.getData() == null || result.getData().getData() == null) {return;}
        try {
            ParcelFileDescriptor pfd = requireActivity().getContentResolver().openFileDescriptor(result.getData().getData(), "rw");
            if(pfd == null) { return; }
            switch (source) {
                case 0: {
                    FileOutputStream outputStream = new FileOutputStream(pfd.getFileDescriptor());
                    SearchFilters filters = new SearchFilters();
                    PrintWriter pw = new PrintWriter(outputStream);
                    Settings settings = ((MainActivity)requireActivity()).getSettings();
                    for(Entry e : DBManager.getDBManagerInstance().getEntries(filters)) {
                        Category c = (e.getValue() > 0 ? settings.getIncomeCategory() : settings.getExpenseCategory()).stream().filter(category -> category.getId() == e.getCategoryId()).findFirst().orElseThrow(RuntimeException::new);
                        SubCategory sc = c.getSubCategories().stream().filter(subCategory -> subCategory.getId() == e.getSubCategoryId()).findFirst().orElseThrow(RuntimeException::new);
                        PaymentType pt = settings.getPaymentMethod().stream().filter(paymentType -> paymentType.getId() == e.getPaymentId()).findFirst().orElseThrow(RuntimeException::new);
                        long id = e.getId() & ~DBManager.IncomeOffset;
                        pw.println(id+";"+MainActivity.userFriendlyDateFormatter.format(e.getDateAndTime())+";"+c.getName()+";"+sc.getName()+";"+pt.getName()+";"+e.getName()+";"+e.getValue());
                    }
                    pw.close();
                    outputStream.close();
                    break;
                }
                case 1: {
                    FileOutputStream outputStream = new FileOutputStream(pfd.getFileDescriptor());
                    Files.copy(requireContext().getDatabasePath(DatabaseDetails.DATABASE_NAME).toPath(), outputStream);
                    outputStream.close();
                    break;
                }
                case 3: {
                    FileOutputStream outputStream = new FileOutputStream(pfd.getFileDescriptor());
                    ((MainActivity) requireContext()).getSettings().exportToJson(outputStream);
                    break;
                }
                case 4:
                    FileInputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
                    ((MainActivity) requireContext()).getSettings().importFromJson(inputStream);
                    DBManager.getDBManagerInstance().insertSettings(((MainActivity) requireContext()).getSettings(), true);
                    break;
            }
            pfd.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, container, false);

        ArrayList<ActivityResultLauncher<Intent>> launchers = new ArrayList<>();
        launchers.add(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            activityCallback(view, result, 0);
        }));
        launchers.add(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            activityCallback(view, result, 1);
        }));
        launchers.add(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            activityCallback(view, result, 2);
        }));
        launchers.add(registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            activityCallback(view, result, 3);
        }));

        RecyclerView recycler = view.findViewById(R.id.settings_recycle_view);
        recycler.setAdapter(new ExportEntryAdapter(launchers));
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setVerticalScrollbarPosition(0);

        return view;
    }
}