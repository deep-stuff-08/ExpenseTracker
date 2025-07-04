package com.example.expensetracker.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.Settings;
import com.example.expensetracker.adapters.ComboBoxAdapter;
import com.example.expensetracker.adapters.SharedUserAdapter;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.Entry;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.example.expensetracker.pojo.User;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class AddExpenseFragment extends Fragment {
    private SimpleDateFormat sdfDate;
    private SimpleDateFormat sdfTime;
    boolean isIncome;
    UnconfirmedEntry unconfirmedEntry;
    public AddExpenseFragment(boolean isIncome, UnconfirmedEntry unconfirmedEntry) {
        this.isIncome = isIncome;
        this.unconfirmedEntry = unconfirmedEntry;
    }
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
        RelativeLayout layoutShared = view.findViewById(R.id.expense_shared_details);
        RecyclerView listSharedUsers = view.findViewById(R.id.expense_sharing_users);
        CheckBox checkBoxIsRepeat = view.findViewById(R.id.expense_repeat);
        Button buttonSubmit = view.findViewById(R.id.expense_submit);
        Spinner sharedExpensePayer = view.findViewById(R.id.expense_shared_payer);

        Settings settings = ((MainActivity)requireActivity()).getSettings();
        Calendar currentTime = Calendar.getInstance();

        ComboBoxAdapter adt_Payment = new ComboBoxAdapter(context, settings.getPaymentMethod(), settings.getIncomeCategory().get(0).getColorId());
        ComboBoxAdapter adt_Category;
        ComboBoxAdapter adt_SubCategory;
        if(isIncome) {
            adt_Category = new ComboBoxAdapter(context, settings.getIncomeCategory(), settings.getIncomeCategory().get(0).getColorId());
            adt_SubCategory =new ComboBoxAdapter(context, settings.getIncomeSubCategory(0),  settings.getIncomeCategory().get(0).getColorId());
        } else {
            adt_Category = new ComboBoxAdapter(context, settings.getExpenseCategory(),  settings.getExpenseCategory().get(0).getColorId());
            adt_SubCategory = new ComboBoxAdapter(context, settings.getExpenseSubCategory(0),  settings.getExpenseCategory().get(0).getColorId());
        }

        ArrayList<String> entryNames;
        if(isIncome) {
            entryNames = DBManager.getDBManagerInstance().getIncomeEntryNames();
        } else {
            entryNames = DBManager.getDBManagerInstance().getExpenseEntryNames();
        }

        HashSet<String> entryNameSet = new HashSet<>(entryNames);

        //Setup Name
        ArrayAdapter<String> adt = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<>(entryNameSet));
        textName.setAdapter(adt);
        textName.setThreshold(1);

        //Setup Value
        final float[] totalValue = {0};
        final float[] actualValue = {0};

        if(unconfirmedEntry != null) {
            TextView textView = view.findViewById(R.id.expense_msg_body);
            textView.setText(unconfirmedEntry.getBody());
            textView.setVisibility(View.VISIBLE);
            textValue.setText(String.valueOf(unconfirmedEntry.getValue()));
            actualValue[0] = unconfirmedEntry.getValue();
        }

        textValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                actualValue[0] = textValue.getText().length() == 0 ? 0 : Float.parseFloat(textValue.getText().toString());
                float currentValue = spinnerShareType.getSelectedItemPosition() == 0 ? actualValue[0] : 100;
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

        //Setup Category / Income Type
        spinnerCategory.setAdapter(adt_Category);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSubCategory.setAdapter(new ComboBoxAdapter(context, isIncome ? settings.getIncomeSubCategory(position) : settings.getExpenseSubCategory(position), ((Category)spinnerCategory.getSelectedItem()).getColorId()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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
        if(isIncome) {
            checkBoxIsShared.setVisibility(View.GONE);
        }
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
                float currentValue = position == 0 ? actualValue[0] : 100;
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
        sharedExpensePayer.setAdapter(new ArrayAdapter<User>(requireContext(), android.R.layout.simple_spinner_item, settings.getUsers()) {
            @Nullable
            @Override
            public User getItem(int position) {
                if(position == 0) {
                    return new User(-1, "Me");
                }
                return super.getItem(position - 1);
            }

            @Override
            public int getCount() {
                return super.getCount() + 1;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                TextView tv = ((TextView) v);
                tv.setText(Objects.requireNonNull(this.getItem(position)).getName());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                TextView tv = ((TextView) v);
                tv.setText(Objects.requireNonNull(this.getItem(position)).getName());
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                return v;
            }
        });

        //Setup Shared User List
        listSharedUsers.setAdapter(new SharedUserAdapter(settings.getUsers(), newTotal -> {
            totalValue[0] = newTotal;
            float currentValue = spinnerShareType.getSelectedItemPosition() == 0 ? actualValue[0] : 100;
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
            Date date = new Date();
            Date time = new Date();
            try {
                date = sdfDate.parse(pickerDate.getText().toString());
                time = sdfTime.parse(pickerTime.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Entry entry = new Entry(textName.getText().toString(), Float.parseFloat(textValue.getText().toString()), ((SubCategory)spinnerSubCategory.getSelectedItem()).getCategoryId(), ((SubCategory)spinnerSubCategory.getSelectedItem()).getId(), spinnerPayment.getSelectedItemPosition() +1, date, time);
            if(!isIncome && checkBoxIsShared.isChecked()) {
                ArrayList<Entry.SharedUser> newUserList = new ArrayList<>();
                ArrayList<Entry.SharedUser> sharedUserList = new ArrayList<>();
                int miscIndex = ((SharedUserAdapter)Objects.requireNonNull(listSharedUsers.getAdapter())).getSharedUserList(sharedUserList, newUserList);
                entry.setValue(((SharedUserAdapter)Objects.requireNonNull(listSharedUsers.getAdapter())).getMeTotal());
                User payer = (User)sharedExpensePayer.getSelectedItem();
                if (newUserList.size() > 0) {
                    StringBuilder userList = new StringBuilder();
                    for (int i = 0; i < newUserList.size(); i++) {
                        userList.append(i + 1).append(") ").append(newUserList.get(i).getUser().getName()).append("\n");
                    }
                    new AlertDialog.Builder(context).setMessage("There are a few names I don't recognize. Should I add them to your user list? If you choose no their split will be added in Misc.\n" + userList)
                            .setPositiveButton("Yes", (dialog, which) -> {
                                newUserList.forEach(sharedUser -> {
                                    long newId = DBManager.getDBManagerInstance().insertUser(sharedUser.getUser(), false);
                                    sharedUser.getUser().setId(newId);
                                });
                                settings.setUsers(DBManager.getDBManagerInstance().getUserData());
                                sharedUserList.addAll(newUserList);
                                entry.setSharedUsersList(payer, sharedUserList);
                                onSuccessfulSubmit(entry, view, unconfirmedEntry, checkBoxIsRepeat.isChecked(), false);
                            })
                            .setCancelable(true)
                            .setNegativeButton("No", (dialog, which) -> {
                                int sum = 0;
                                for(Entry.SharedUser users : newUserList) {
                                    sum += users.getValue();
                                }
                                if(miscIndex == -1) {
                                    User miscUser = new User("Misc");
                                    for(User u : settings.getUsers()) {
                                        if(u.getName().equals("Misc")){
                                            miscUser = u;
                                        }
                                    }
                                    sharedUserList.add(new Entry.SharedUser(miscUser, sum));
                                } else {
                                    sharedUserList.get(miscIndex).setValue(sharedUserList.get(miscIndex).getValue() + sum);
                                }
                                sharedExpensePayer.getSelectedItem();
                                entry.setSharedUsersList(payer, sharedUserList);
                                onSuccessfulSubmit(entry, view, unconfirmedEntry, checkBoxIsRepeat.isChecked(), false);
                            }).show();
                } else {
                    entry.setSharedUsersList(payer, sharedUserList);
                    onSuccessfulSubmit(entry, view, unconfirmedEntry, checkBoxIsRepeat.isChecked(), false);
                }
            } else {
                onSuccessfulSubmit(entry, view, unconfirmedEntry, checkBoxIsRepeat.isChecked(), isIncome);
            }
        });
    }

    private void onSuccessfulSubmit(Entry entry, View view, UnconfirmedEntry unconfirmedEntry, boolean isRepeat, boolean isIncome) {
        if(unconfirmedEntry != null) {
            DBManager.getDBManagerInstance().deleteUnconfirmedEntries(unconfirmedEntry.getId());
        }
        if(isIncome) {
            DBManager.getDBManagerInstance().insertIncomeEntries(entry);
        } else {
            DBManager.getDBManagerInstance().insertExpenseEntries(entry);
        }
        Snackbar.make(view, "Entry Saved Successfully", Snackbar.LENGTH_SHORT).show();
        if (isRepeat) {
            Navigation.findNavController(view).navigate(R.id.entryFragment, null, new NavOptions.Builder().setPopUpTo(R.id.entryFragment, true).build());
        } else {
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        }
    }
}