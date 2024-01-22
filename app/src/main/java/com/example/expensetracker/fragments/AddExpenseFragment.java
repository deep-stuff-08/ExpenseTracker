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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.expensetracker.adapters.ComboBoxAdapter;
import com.example.expensetracker.ExpenseSettings;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.SharedUserAdapter;
import com.example.expensetracker.pojo.Expense;
import com.example.expensetracker.pojo.UnconfirmedEntry;
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
        sdfDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault());
        sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context context = requireContext();
        AutoCompleteTextView textName = view.findViewById(R.id.expense_name);
        EditText textValue = view.findViewById(R.id.expense_value);
        Spinner spinnerShareType = view.findViewById(R.id.expense_sharing_type);
        TextView textSharingValue = view.findViewById(R.id.expense_sharing_value);
        Spinner spinnerPayment = view.findViewById(R.id.expense_payment);
        Spinner spinnerCategory = view.findViewById(R.id.expense_category);
        Spinner spinnerSubCategory = view.findViewById(R.id.expense_subcategory);
        CheckBox checkBoxIsShared = view.findViewById(R.id.expense_shared);
        EditText pickerDate = view.findViewById(R.id.expense_date);
        EditText pickerTime = view.findViewById(R.id.expense_time);
        RadioGroup radioCreditDebit = view.findViewById(R.id.expense_income);
        RelativeLayout layoutShared = view.findViewById(R.id.expense_shared_details);
        RecyclerView listSharedUsers = view.findViewById(R.id.expense_sharing_users);
        CheckBox checkBoxIsRepeat = view.findViewById(R.id.expense_repeat);
        Button buttonSubmit = view.findViewById(R.id.expense_submit);

        ExpenseSettings settings = ((MainActivity)requireActivity()).getSettings();
        Calendar currentTime = Calendar.getInstance();

        int bgId = settings.getCategory().get(0).getColorId();
        ComboBoxAdapter adt_Payment = new ComboBoxAdapter(context, settings.getPaymentMethod(), bgId);
        ComboBoxAdapter adt_Category = new ComboBoxAdapter(context, settings.getCategory(), bgId);
        ComboBoxAdapter adt_Type = new ComboBoxAdapter(context, settings.getTypes(), R.color.grey);
        ComboBoxAdapter adt_SubCategory = new ComboBoxAdapter(context, settings.getSubCategory(0), bgId);
        ComboBoxAdapter adt_User = new ComboBoxAdapter(context, settings.getUsers(), R.color.grey);
        ComboBoxAdapter adt_Income = new ComboBoxAdapter(context, settings.getIncomeSources(), R.color.grey);

        //Setup Name
        ArrayAdapter<String> adt = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new String[]{"Deep", "Fee"});
        textName.setAdapter(adt);
        textName.setThreshold(1);

        //Setup Value
        UnconfirmedEntry unconfirmedEntry = null;
        Bundle arg = getArguments();
        final int unconfirmedEntryId = arg == null ? -1 : arg.getInt("unconfirmedEntryId", -1);
        if(unconfirmedEntryId >= 0) {
            unconfirmedEntry = ((MainActivity) requireActivity()).getEntries().get(unconfirmedEntryId);
            TextView textView = view.findViewById(R.id.expense_msg_body);
            textView.setText(unconfirmedEntry.getBody());
            textView.setVisibility(View.VISIBLE);
            textValue.setText(String.valueOf(unconfirmedEntry.getValue()));
        }
        final int[] totalValue = {0};
        final int[] actualValue = {0};
        textValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualValue[0] = textValue.getText().length() == 0 ? 0 : Integer.parseInt(textValue.getText().toString());
                int currentValue = spinnerShareType.getSelectedItemPosition() == 0 ? actualValue[0] : 100;
                textSharingValue.setText(getString(R.string.value_div, totalValue[0], currentValue));
                if(totalValue[0] == currentValue) {
                    textSharingValue.setTextColor(getResources().getColor(R.color.categoryGreen, context.getTheme()));
                } else {
                    textSharingValue.setTextColor(getResources().getColor(R.color.categoryOrange, context.getTheme()));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        //Setup Sharing Value
        textSharingValue.setText(getString(R.string.value_div, totalValue[0], actualValue[0]));
        textSharingValue.setTextColor(getResources().getColor(R.color.categoryGreen, context.getTheme()));

        //Setup Credit/Debit Radio Group
        RadioButton expenseIncomeCredit = view.findViewById(R.id.expense_income_credit);
        if(unconfirmedEntry != null) {
            expenseIncomeCredit.setChecked(unconfirmedEntry.isCredited());
            RadioButton expenseIncomeDebit = view.findViewById(R.id.expense_income_debit);
            expenseIncomeDebit.setChecked(!unconfirmedEntry.isCredited());
        }
        radioCreditDebit.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.expense_income_credit) {
                checkBoxIsShared.setChecked(false);
                layoutShared.setVisibility(View.GONE);
                checkBoxIsShared.setEnabled(false);
                spinnerCategory.setAdapter(adt_Type);
                spinnerSubCategory.setAdapter(adt_Income);
            } else if(checkedId == R.id.expense_income_debit) {
                checkBoxIsShared.setEnabled(true);
                spinnerCategory.setAdapter(adt_Category);
                spinnerSubCategory.setAdapter(adt_SubCategory);
            }
        });

        //Setup Category / Income Type
        spinnerCategory.setAdapter(adt_Category);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                if(((RadioButton)view.findViewById(R.id.expense_income_credit)).isChecked()) {
                    if(position == 0) {
                        spinnerSubCategory.setAdapter(adt_Income);
                    } else if(position == 1) {
                        spinnerSubCategory.setAdapter(adt_User);
                    }
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).setBackgroundColorId(R.color.grey);
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).notifyDataSetChanged();
                } else {
                    int bgId = settings.getCategory().get(position).getColorId();
                    spinnerSubCategory.setAdapter(new ComboBoxAdapter(requireContext(), settings.getSubCategory(position), bgId));
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).setBackgroundColorId(bgId);
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Setup SubCategory
        spinnerSubCategory.setAdapter(adt_SubCategory);

        //Setup Payment Type
        spinnerPayment.setAdapter(adt_Payment);

        //Setup Date
        if(unconfirmedEntry != null) {
            pickerDate.setText(sdfDate.format(unconfirmedEntry.getSentDate().getTime()));
        } else {
            pickerDate.setText(sdfDate.format(currentTime.getTime()));
        }
        pickerDate.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            try {
                Date d = sdfDate.parse(pickerDate.getText().toString());
                if(d != null) {
                    calendar.setTime(d);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            new DatePickerDialog(requireContext(), (view22, year, month, dayOfMonth) -> {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth);
                pickerDate.setText(sdfDate.format(c.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        //Setup Time
        if(unconfirmedEntry != null) {
            pickerTime.setText(sdfTime.format(unconfirmedEntry.getSentDate().getTime()));
        } else {
            pickerTime.setText(sdfTime.format(currentTime.getTime()));
        }
        pickerTime.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            try {
                Date d = sdfTime.parse(pickerTime.getText().toString());
                if(d != null) {
                    calendar.setTime(d);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            new TimePickerDialog(getContext(), (view23, hourOfDay, minute) -> {
                Calendar c = Calendar.getInstance();
                c.set(0, 0, 0, hourOfDay, minute);
                pickerTime.setText(sdfTime.format(c.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
        });

        //Setup IsShared Checkbox
        checkBoxIsShared.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                layoutShared.setVisibility(View.VISIBLE);
            } else {
                layoutShared.setVisibility(View.GONE);
            }
        });

        //Setup Sharing Rupee or Percent
        spinnerShareType.setAdapter(new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, new String[] {"Rs", "%"}) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = ((TextView) v);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = ((TextView) v);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                return v;
            }
        });

        spinnerShareType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int currentValue = position == 0 ? actualValue[0] : 100;
                textSharingValue.setText(getString(R.string.value_div, totalValue[0], currentValue));
                if(totalValue[0] == currentValue) {
                    textSharingValue.setTextColor(getResources().getColor(R.color.categoryGreen, context.getTheme()));
                } else {
                    textSharingValue.setTextColor(getResources().getColor(R.color.categoryOrange, context.getTheme()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Setup Shared User List
        listSharedUsers.setAdapter(new SharedUserAdapter(settings.getUsers(), newTotal -> {
            totalValue[0] = newTotal;
            int currentValue = spinnerShareType.getSelectedItemPosition() == 0 ? actualValue[0] : 100;
            textSharingValue.setText(getString(R.string.value_div, totalValue[0], currentValue));
            if(totalValue[0] == actualValue[0]) {
                textSharingValue.setTextColor(getResources().getColor(R.color.categoryGreen, context.getTheme()));
            } else {
                textValue.setTextColor(getResources().getColor(R.color.categoryOrange, context.getTheme()));
            }
        }));
        listSharedUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        listSharedUsers.setVerticalScrollbarPosition(0);

        //Setup IsLogMulti
        if(unconfirmedEntry == null) {
            if (getArguments() != null) {
                checkBoxIsRepeat.setChecked(getArguments().getBoolean("isLogMultiOn", false));
            }
        } else {
            checkBoxIsRepeat.setEnabled(false);
        }

        //Setup Submit Button
        buttonSubmit.setOnClickListener(view1 -> {
            boolean error = false;
            if(textName.getText().length() == 0) {
                textName.setError("Field not filled");
                error = true;
            }
            if(textValue.getText().length() == 0) {
                textValue.setError("Field not filled");
                error = true;
            }
            if(checkBoxIsShared.isChecked() && actualValue[0] != totalValue[0]) {
                textSharingValue.setError("Value not adding up");
                error = true;
            }
            if(error) {
                return;
            }
            Snackbar.make(view, "Entry Saved Successfully", Snackbar.LENGTH_SHORT).show();
            if(unconfirmedEntryId >= 0) {
                ((MainActivity)requireActivity()).getEntries().remove(unconfirmedEntryId);
            }
            try {
                Expense expense = new Expense(textName.getText().toString(), Integer.parseInt(textValue.getText().toString()), !expenseIncomeCredit.isChecked(), spinnerCategory.getSelectedItemPosition(), spinnerSubCategory.getSelectedItemPosition(), spinnerPayment.getSelectedItemPosition(), sdfDate.parse(pickerDate.getText().toString()), sdfTime.parse(pickerTime.getText().toString()), checkBoxIsShared.isChecked());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (checkBoxIsRepeat.isChecked()) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLogMultiOn", true);
                Navigation.findNavController(view).navigate(R.id.addExpenseFragment, bundle, new NavOptions.Builder().setPopUpTo(R.id.addExpenseFragment, true).build());
            } else {
                requireActivity().getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }
}