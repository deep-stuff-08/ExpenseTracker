package com.example.expensetracker;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        AutoCompleteTextView expenseName = view.findViewById(R.id.expense_name);
        Context context = getContext();
        if(context != null) {
            ArrayAdapter<String> adt = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new String[]{"Deep", "Fee"});
            expenseName.setAdapter(adt);
            expenseName.setThreshold(1);

            ExpenseSettings settings = ((MainActivity)requireActivity()).getSettings();

            EditText expenseValue = view.findViewById(R.id.expense_value);
            expenseValue.getText();

            Spinner expenseCategory = view.findViewById(R.id.expense_category);
            SpinnerCategoryAdapter string_adt = new SpinnerCategoryAdapter(context, settings.getPaymentMethod());
            expenseCategory.setAdapter(string_adt);

            Spinner expenseSubCategory = view.findViewById(R.id.expense_subcategory);
            SpinnerCategoryAdapter string_adt2 = new SpinnerCategoryAdapter(context, settings.getCategory());
            expenseSubCategory.setAdapter(string_adt2);

            Spinner expensePayment = view.findViewById(R.id.expense_payment);
            SpinnerCategoryAdapter string_adt3 = new SpinnerCategoryAdapter(context, settings.getPaymentMethod());
            expensePayment.setAdapter(string_adt3);
        }
        Calendar currentTime = Calendar.getInstance();

        EditText expenseDate = view.findViewById(R.id.expense_date);
        expenseDate.setText(sdfDate.format(currentTime.getTime()));
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
            Dialog dialog = new DatePickerDialog(getContext(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });
        EditText expenseTime = view.findViewById(R.id.expense_time);
        expenseTime.setText(sdfTime.format(currentTime.getTime()));
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
        expenseShared.isChecked();
        CheckBox expenseRepeat = view.findViewById(R.id.expense_repeat);
        if(getArguments() != null) {
            expenseRepeat.setChecked(getArguments().getBoolean("isLogMultiOn", false));
        }
        Button expenseSubmit = view.findViewById(R.id.expense_submit);
        expenseSubmit.setOnClickListener(view1 -> {
            Snackbar.make(view, "Entry Saved Successfully", Snackbar.LENGTH_SHORT).show();
            if(expenseRepeat.isChecked()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLogMultiOn", true);
                Navigation.findNavController(view).navigate(R.id.addExpenseFragment, bundle, new NavOptions.Builder().setPopUpTo(R.id.addExpenseFragment, true).build());
            } else {
                if(getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
    }
}