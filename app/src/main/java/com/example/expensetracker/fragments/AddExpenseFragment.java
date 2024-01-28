package com.example.expensetracker.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.expensetracker.adapters.ComboBoxAdapter;
import com.example.expensetracker.ExpenseSettings;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.SharedUserAdapter;
import com.example.expensetracker.pojo.Entry;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

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

        int expBgId = settings.getExpenseCategory().get(0).getColorId();
        int incBgId = settings.getIncomeCategory().get(0).getColorId();
        ComboBoxAdapter adt_Payment = new ComboBoxAdapter(context, settings.getPaymentMethod(), expBgId);
        ComboBoxAdapter adt_ExpenseCategory = new ComboBoxAdapter(context, settings.getExpenseCategory(), expBgId);
        ComboBoxAdapter adt_ExpenseSubCategory = new ComboBoxAdapter(context, settings.getExpenseSubCategory(0), expBgId);
        ComboBoxAdapter adt_IncomeCategory = new ComboBoxAdapter(context, settings.getExpenseCategory(), incBgId);
        ComboBoxAdapter adt_IncomeSubCategory = new ComboBoxAdapter(context, settings.getExpenseSubCategory(0), incBgId);

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
                spinnerCategory.setAdapter(adt_IncomeCategory);
                spinnerSubCategory.setAdapter(adt_IncomeSubCategory);
            } else if(checkedId == R.id.expense_income_debit) {
                checkBoxIsShared.setEnabled(true);
                spinnerCategory.setAdapter(adt_ExpenseCategory);
                spinnerSubCategory.setAdapter(adt_ExpenseSubCategory);
            }
        });

        //Setup Category / Income Type
        spinnerCategory.setAdapter(adt_ExpenseCategory);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                if(((RadioButton)view.findViewById(R.id.expense_income_credit)).isChecked()) {
                    int bgId = settings.getExpenseCategory().get(position).getColorId();
                    spinnerSubCategory.setAdapter(new ComboBoxAdapter(requireContext(), settings.getIncomeSubCategory(position), bgId));
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).setBackgroundColorId(bgId);
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).notifyDataSetChanged();
                } else {
                    int bgId = settings.getExpenseCategory().get(position).getColorId();
                    spinnerSubCategory.setAdapter(new ComboBoxAdapter(requireContext(), settings.getExpenseSubCategory(position), bgId));
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).setBackgroundColorId(bgId);
                    ((ComboBoxAdapter) spinnerPayment.getAdapter()).notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //Setup SubCategory
        spinnerSubCategory.setAdapter(adt_ExpenseSubCategory);

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
                textSharingValue.setTextColor(getResources().getColor(R.color.categoryOrange, context.getTheme()));
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
            if(unconfirmedEntryId >= 0) {
                ((MainActivity)requireActivity()).getEntries().remove(unconfirmedEntryId);
            }
            ArrayList<String> newUsers = new ArrayList<>();
            try {
                ArrayList<Entry.SharedUser> sharedUserList =  ((SharedUserAdapter)Objects.requireNonNull(listSharedUsers.getAdapter())).getSharedUserList();
                HashMap<String, Integer> userMap = new HashMap<>();
                for(Entry.SharedUser sharedUser : sharedUserList) {
                    if(userMap.containsKey(sharedUser.getName())) {
                        userMap.replace(sharedUser.getName(), userMap.getOrDefault(sharedUser.getName(), 0) + sharedUser.getValue());
                    } else if(!sharedUser.getName().isEmpty()){
                        userMap.put(sharedUser.getName(), sharedUser.getValue());
                    }
                }
                sharedUserList.clear();
                Set<String> s = new HashSet<>();
                settings.getUsers().forEach(user -> s.add(user.getName()));
                for(String name : userMap.keySet()) {
                    if(!s.contains(name) && !"Me".equals(name)) {
                        newUsers.add(name);
                    }
                    sharedUserList.add(new Entry.SharedUser(name, userMap.getOrDefault(name, 0)));
                }
                Entry entry = new Entry(textName.getText().toString(), Integer.parseInt(textValue.getText().toString()), spinnerCategory.getSelectedItemPosition(), spinnerSubCategory.getSelectedItemPosition(), spinnerPayment.getSelectedItemPosition(), sdfDate.parse(pickerDate.getText().toString()), sdfTime.parse(pickerTime.getText().toString()), sharedUserList);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(newUsers.size() > 0) {
                StringBuilder userList = new StringBuilder();
                for(int i = 0; i < newUsers.size(); i++) {
                    userList.append(i).append(") ").append(newUsers.get(i)).append("\n");
                }
                new AlertDialog.Builder(context).setMessage("There are a few names I don't recognize. Should I add them to your user list? If you choose no their split will be added in Misc.\n" + userList)
                        .setPositiveButton("Yes", (dialog, which) -> {
                            onSuccessfulSubmit(view, checkBoxIsRepeat);
                        })
                        .setCancelable(true)
                        .setNegativeButton("No", (dialog, which) -> {
                            onSuccessfulSubmit(view, checkBoxIsRepeat);
                        }).show();
            } else {
                onSuccessfulSubmit(view, checkBoxIsRepeat);
            }
        });
    }

    private void onSuccessfulSubmit(View view, CheckBox isRepeat) {
        Snackbar.make(view, "Entry Saved Successfully", Snackbar.LENGTH_SHORT).show();
        if (isRepeat.isChecked()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isLogMultiOn", true);
            Navigation.findNavController(view).navigate(R.id.entryFragment, bundle, new NavOptions.Builder().setPopUpTo(R.id.entryFragment, true).build());
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }
}