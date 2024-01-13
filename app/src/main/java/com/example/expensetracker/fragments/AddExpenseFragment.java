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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.expensetracker.adapters.ComboBoxAdapter;
import com.example.expensetracker.ExpenseSettings;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.SharedUserAdapter;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
        ComboBoxAdapter adt_Payment = new ComboBoxAdapter(context, settings.getPaymentMethod(), bgId);
        expensePayment.setAdapter(adt_Payment);

        Spinner expenseCategory = view.findViewById(R.id.expense_category);
        ComboBoxAdapter adt_Category = new ComboBoxAdapter(context, settings.getCategory(), bgId);
        ComboBoxAdapter adt_Type = new ComboBoxAdapter(context, settings.getTypes(), R.color.grey);
        expenseCategory.setAdapter(adt_Category);

        Spinner expenseSubCategory = view.findViewById(R.id.expense_subcategory);
        ComboBoxAdapter adt_SubCategory = new ComboBoxAdapter(context, settings.getSubCategory(0), bgId);
        ComboBoxAdapter adt_User = new ComboBoxAdapter(context, settings.getUsers(), R.color.grey);
        ComboBoxAdapter adt_Income = new ComboBoxAdapter(context, settings.getIncomeSources(), R.color.grey);
        expenseSubCategory.setAdapter(adt_SubCategory);

        Spinner expenseShareType = view.findViewById(R.id.expense_sharing_type);
        expenseShareType.setAdapter(new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, new String[] {"Rs", "%"}) {
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

        expenseCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view2, int position, long id) {
                if(((RadioButton)view.findViewById(R.id.expense_income_credit)).isChecked()) {
                    if(position == 0) {
                        expenseSubCategory.setAdapter(adt_Income);
                    } else if(position == 1) {
                        expenseSubCategory.setAdapter(adt_User);
                    }
                    ((ComboBoxAdapter) expensePayment.getAdapter()).setBackgroundColorId(R.color.grey);
                    ((ComboBoxAdapter) expensePayment.getAdapter()).notifyDataSetChanged();
                } else {// if(view.findViewById(R.id.expense_income_credit).isSelected()) {
                    int bgId = settings.getCategory().get(position).getColorId();
                    expenseSubCategory.setAdapter(new ComboBoxAdapter(requireContext(), settings.getSubCategory(position), bgId));
                    ((ComboBoxAdapter) expensePayment.getAdapter()).setBackgroundColorId(bgId);
                    ((ComboBoxAdapter) expensePayment.getAdapter()).notifyDataSetChanged();
                }
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

        RadioGroup expenseIncome = view.findViewById(R.id.expense_income);
        expenseIncome.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.expense_income_credit) {
                expenseShared.setEnabled(false);
                expenseCategory.setAdapter(adt_Type);
                expenseSubCategory.setAdapter(adt_Income);
            } else if(checkedId == R.id.expense_income_debit) {
                expenseShared.setEnabled(true);
                expenseCategory.setAdapter(adt_Category);
                expenseSubCategory.setAdapter(adt_SubCategory);
            }
        });

        RecyclerView expenseSharedList = view.findViewById(R.id.expense_sharing_users);
        expenseSharedList.setAdapter(new SharedUserAdapter(settings.getUsers()));
        expenseSharedList.setLayoutManager(new LinearLayoutManager(getActivity()));
        expenseSharedList.setVerticalScrollbarPosition(0);

        if(unconfirmedEntry != null) {
            RadioButton expenseIncomeCredit = view.findViewById(R.id.expense_income_credit);
            expenseIncomeCredit.setChecked(unconfirmedEntry.isCredited());
            RadioButton expenseIncomeDebit = view.findViewById(R.id.expense_income_debit);
            expenseIncomeDebit.setChecked(!unconfirmedEntry.isCredited());
        }

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