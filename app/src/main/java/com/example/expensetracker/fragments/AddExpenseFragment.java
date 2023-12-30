package com.example.expensetracker.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetracker.adapters.SpinnerCategoryAdapter;
import com.example.expensetracker.ExpenseSettings;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {
    private SimpleDateFormat sdfDate;
    private SimpleDateFormat sdfTime;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UnconfirmedEntry unconfirmedEntry = null;
        Bundle arg = getArguments();
        final int unconfirmedEntryId = arg == null ? -1 : arg.getInt("unconfirmedEntryId", -1);
        if(unconfirmedEntryId >= 0) {
            unconfirmedEntry = ((MainActivity) requireActivity()).getEntries().get(unconfirmedEntryId);
        }

        if(unconfirmedEntry != null) {
            TextView textView = view.findViewById(R.id.expense_msg_body);
            textView.setText(unconfirmedEntry.getBody());
            textView.setVisibility(View.VISIBLE);
        }

        AutoCompleteTextView expenseName = view.findViewById(R.id.expense_name);
        Context context = requireContext();

        ArrayAdapter<String> adt = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new String[]{"Deep", "Fee"});
        expenseName.setAdapter(adt);
        expenseName.setThreshold(1);

        ExpenseSettings settings = ((MainActivity)requireActivity()).getSettings();

        EditText expenseValue = view.findViewById(R.id.expense_value);
        if(unconfirmedEntry != null) {
            expenseValue.setText(String.valueOf(unconfirmedEntry.getValue()));
        }

        int bgId = settings.getCategory().get(0).getColorId();

        Spinner expensePayment = view.findViewById(R.id.expense_payment);
        SpinnerCategoryAdapter string_adt3 = new SpinnerCategoryAdapter(context, settings.getPaymentMethod(), bgId);
        expensePayment.setAdapter(string_adt3);

        Spinner expenseCategory = view.findViewById(R.id.expense_category);
        SpinnerCategoryAdapter string_adt = new SpinnerCategoryAdapter(context, settings.getCategory(), bgId);
        expenseCategory.setAdapter(string_adt);

        Spinner expenseSubCategory = view.findViewById(R.id.expense_subcategory);
        SpinnerCategoryAdapter string_adt2 = new SpinnerCategoryAdapter(context, settings.getSubCategory(0), bgId);
        expenseSubCategory.setAdapter(string_adt2);
        expenseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int bgId = settings.getCategory().get(position).getColorId();
                expenseSubCategory.setAdapter(new SpinnerCategoryAdapter(requireContext(), settings.getSubCategory(position), bgId));
                ((SpinnerCategoryAdapter)expensePayment.getAdapter()).setBackgroundColorId(bgId);
                ((SpinnerCategoryAdapter) expensePayment.getAdapter()).notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Calendar currentTime = Calendar.getInstance();

        EditText expenseDate = view.findViewById(R.id.expense_date);
        if(unconfirmedEntry != null) {
            expenseDate.setText(sdfDate.format(unconfirmedEntry.getSentDate().getTime()));
        } else {
            expenseDate.setText(sdfDate.format(currentTime.getTime()));
        }
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            expenseDate.setText(sdfDate.format(c.getTime()));
        };
        expenseDate.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            try {
                Date d = sdfDate.parse(expenseDate.getText().toString());
                if(d != null) {
                    calendar.setTime(d);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Dialog dialog = new DatePickerDialog(requireContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        EditText expenseTime = view.findViewById(R.id.expense_time);
        if(unconfirmedEntry != null) {
            expenseTime.setText(sdfTime.format(unconfirmedEntry.getSentDate().getTime()));
        } else {
            expenseTime.setText(sdfTime.format(currentTime.getTime()));
        }
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, minute) -> {
            Calendar c = Calendar.getInstance();
            c.set(0, 0, 0, hour, minute);
            expenseTime.setText(sdfTime.format(c.getTime()));
        };
        expenseTime.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            try {
                Date d = sdfTime.parse(expenseTime.getText().toString());
                if(d != null) {
                    calendar.setTime(d);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Dialog dialog = new TimePickerDialog(getContext(), timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            dialog.show();
        });

        CheckBox expenseShared = view.findViewById(R.id.expense_shared);
        //noinspection ResultOfMethodCallIgnored
        expenseShared.isChecked();
        CheckBox expenseRepeat = view.findViewById(R.id.expense_repeat);
        if(unconfirmedEntry == null) {
            if (getArguments() != null) {
                expenseRepeat.setChecked(getArguments().getBoolean("isLogMultiOn", false));
            }
        } else {
            expenseRepeat.setEnabled(false);
        }
        Button expenseSubmit = view.findViewById(R.id.expense_submit);
        expenseSubmit.setOnClickListener(view1 -> {

            Category category = new Category("Deep", 0, new ArrayList<>());

            boolean error = false;
            if(expenseName.getText().length() == 0) {
                expenseName.setError("Field not filled");
                error = true;
            }
            if(expenseValue.getText().length() == 0) {
                expenseValue.setError("Field not filled");
                error = true;
            }
            if(error) {
                return;
            }
            Snackbar.make(view, "Entry Saved Successfully", Snackbar.LENGTH_SHORT).show();
            if(unconfirmedEntryId >= 0) {
                ((MainActivity)requireActivity()).getEntries().remove(unconfirmedEntryId);
            }
            if (expenseRepeat.isChecked()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLogMultiOn", true);
                Navigation.findNavController(view).navigate(R.id.addExpenseFragment, bundle, new NavOptions.Builder().setPopUpTo(R.id.addExpenseFragment, true).build());
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
}