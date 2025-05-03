package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.SearchFilters;
import com.example.expensetracker.adapters.ExpEntryAdapter;
import com.example.expensetracker.adapters.ExpItemDetailsLookup;
import com.example.expensetracker.adapters.ExpItemKeyProvider;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.Entry;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    SearchFilters filter;
    RecyclerView recycler;
    SelectionTracker<Long> selectionTracker;
    ArrayList<Entry> entries;
    SelectionTracker.SelectionObserver<Long> observer;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void refreshEntries() {
        entries = DBManager.getDBManagerInstance().getEntries(filter);
        ExpEntryAdapter adapter = new ExpEntryAdapter((MainActivity)requireActivity(), entries);
        recycler.setAdapter(adapter);
        selectionTracker = new SelectionTracker.Builder<>("HomeSelector", recycler, new ExpItemKeyProvider(entries), new ExpItemDetailsLookup(recycler), StorageStrategy.createLongStorage())
                        .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                        .build();
        adapter.setSelectionTracker(selectionTracker);
        selectionTracker.addObserver(observer);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity activity = (MainActivity)requireActivity();

        filter = new SearchFilters();

        recycler = view.findViewById(R.id.recycleView);
        entries = DBManager.getDBManagerInstance().getEntries(filter);
        ExpEntryAdapter adapter = new ExpEntryAdapter(activity, entries);
        recycler.setAdapter(adapter);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setVerticalScrollbarPosition(0);

        selectionTracker =
                new SelectionTracker.Builder<>("HomeSelector", recycler, new ExpItemKeyProvider(entries), new ExpItemDetailsLookup(recycler), StorageStrategy.createLongStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build();

        adapter.setSelectionTracker(selectionTracker);
        observer = new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onItemStateChanged(@NonNull Long key, boolean selected) {
                super.onItemStateChanged(key, selected);
                if(selectionTracker.hasSelection()) {
                    activity.getDeleteButton().setVisibility(View.VISIBLE);
                    for(int i = 0; i < Math.min(recycler.getChildCount(),entries.size()); i++) {
                        recycler.getChildAt(i).findViewById(R.id.expense_is_selected).setVisibility(View.VISIBLE);
                    }
                } else {
                    activity.getDeleteButton().setVisibility(View.GONE);
                    for(int i = 0; i < Math.min(recycler.getChildCount(),entries.size()); i++) {
                        recycler.getChildAt(i).findViewById(R.id.expense_is_selected).setVisibility(View.GONE);
                    }
                }
            }
        };
        selectionTracker.addObserver(observer);

        String[] typeArray = {
          "All", "Expense", "Income"
        };
        TextView textFilterType = view.findViewById(R.id.filter_type);
        textFilterType.setText(typeArray[0]);
        textFilterType.setClickable(true);
        textFilterType.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, typeArray), (dialog1, which) -> {
                filter.setType(which);
                refreshEntries();
                textFilterType.setText(typeArray[which]);
            }).setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss()).show();
        });

        String[] timeArray = {
                "This Month", "Last Month", "Last 6 Months", "Last Year", "Custom"
        };
        Date[] timeDateArray = new Date[timeArray.length - 1];
        for(int i = 0; i < timeArray.length - 1; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            int month;
            switch(i) {
                case 0:
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    break;
                case 1:
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    month = calendar.get(Calendar.MONTH);
                    if(month == Calendar.JANUARY) {
                        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    } else {
                        calendar.set(Calendar.MONTH, month - 1);
                    }
                    break;
                case 2:
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    month = calendar.get(Calendar.MONTH);
                    if(month - 5 < 0) {
                        int leftover = month - 5;
                        calendar.set(Calendar.MONTH, Calendar.DECEMBER + leftover);
                        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    } else {
                        calendar.set(Calendar.MONTH, month - 5);
                    }
                    break;
                case 3:
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
                    break;
            }
            timeDateArray[i] = calendar.getTime();
        }

        TextView textFilterDate = view.findViewById(R.id.filter_date);
        textFilterDate.setText(timeArray[0]);
        textFilterDate.setClickable(true);
        textFilterDate.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            dialog.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, timeArray), (dialog1, which) -> {
                textFilterDate.setText(timeArray[which]);
                if(timeArray[which].equals("Custom")) {
                    MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
                    builder.setTitleText("Select date range");
                    MaterialDatePicker<Pair<Long, Long>> datePicker = builder.build();
                    datePicker.addOnPositiveButtonClickListener(selection -> {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        Date startDateObj = new Date(selection.first);
                        Date endDateObj = new Date(selection.second);
                        String startDateString = sdf.format(startDateObj);
                        String endDateString = sdf.format(endDateObj);
                        String selectedDateRange = startDateString + " - " + endDateString;
                        textFilterDate.setText(selectedDateRange);
                        filter.setDate(startDateObj, endDateObj);
                    });
                    datePicker.show(getParentFragmentManager(), "DATE_PICKER");
                } else {
                    filter.setDate(timeDateArray[which], new Date());
                }
                refreshEntries();
            }).setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss()).show();
        });

        TextView textFilterPayment = view.findViewById(R.id.filter_payment);
        textFilterPayment.setText(R.string.string_all);
        boolean[] paymentData = {false, false, false};
        textFilterPayment.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            ArrayList<String> items = new ArrayList<>();
            activity.getSettings().getPaymentMethod().forEach(paymentType -> items.add(paymentType.getName()));
            dialog.setMultiChoiceItems(items.toArray(new String[]{}), paymentData, (dialog1, which, isChecked) -> paymentData[which] = isChecked)
                    .setPositiveButton("Ok", (dialog1, which) -> {
                StringBuilder custom = new StringBuilder();
                boolean areAllSelected = true;
                boolean areNoneSelected = false;
                ArrayList<Long> list = new ArrayList<>();
                for(int i = 0; i < items.size(); i++) {
                    areAllSelected = areAllSelected && paymentData[i];
                    areNoneSelected = areNoneSelected || paymentData[i];
                    if(paymentData[i]) {
                        custom.append(items.get(i)).append(" ");
                        list.add(activity.getSettings().getPaymentMethod().get(i).getId());
                    }
                }
                if(areAllSelected || !areNoneSelected){
                    textFilterPayment.setText(R.string.string_all);
                    list.clear();
                } else {
                    textFilterPayment.setText(custom.toString());
                }
                filter.setPaymentType(list);
                refreshEntries();
            }).setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss()).show();
        });

        TextView textFilterCategory = view.findViewById(R.id.filter_category);
        ArrayList<String> items = new ArrayList<>();
        if(textFilterType.getText().equals("All")) {
            activity.getSettings().getExpenseCategory().forEach(paymentType -> items.add(paymentType.getName()));
            activity.getSettings().getIncomeCategory().forEach(paymentType -> items.add(paymentType.getName()));
        } else if (textFilterType.getText().equals("Expense")) {
            activity.getSettings().getExpenseCategory().forEach(paymentType -> items.add(paymentType.getName()));
        } else if (textFilterType.getText().equals("Income")) {
            activity.getSettings().getIncomeCategory().forEach(paymentType -> items.add(paymentType.getName()));
        }
        boolean[] categoryData = new boolean[items.size()];
        textFilterCategory.setText(R.string.string_all);
        textFilterCategory.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

            dialog.setMultiChoiceItems(items.toArray(new String[]{}), categoryData, (dialog1, which, isChecked) -> categoryData[which] = isChecked).setPositiveButton("Ok", (dialog1, which) -> {
                StringBuilder custom = new StringBuilder();
                ArrayList<Long> list = new ArrayList<>();
                boolean areAllSelected = true;
                boolean areNoneSelected = false;
                for(int i = 0; i < items.size(); i++) {
                    areAllSelected = areAllSelected && categoryData[i];
                    areNoneSelected = areNoneSelected || categoryData[i];
                    if(categoryData[i]) {
                        custom.append(items.get(i)).append(" ");
                        int expenseLen = activity.getSettings().getExpenseCategory().size();
                        if(i >= expenseLen) {
                            list.add(activity.getSettings().getIncomeCategory().get(i - expenseLen).getId());
                        } else {
                            list.add(activity.getSettings().getExpenseCategory().get(i).getId());
                        }
                    }
                }
                if(areAllSelected || !areNoneSelected){
                    list.clear();
                    textFilterCategory.setText(R.string.string_all);
                } else {
                    textFilterCategory.setText(custom.toString());
                }
                filter.setCategory(list);
                refreshEntries();
            }).setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss()).show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.fab).setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_entryFragment));
        view.findViewById(R.id.unconfirmed_parent).setOnClickListener(view2 -> Navigation.findNavController(view2).navigate(R.id.action_homeFragment_to_unconfirmedEntryFragment));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DBManager.getDBManagerInstance().getUnconfirmedEntries().size() > 0) {
            requireView().findViewById(R.id.unconfirmed_parent).setVisibility(View.VISIBLE);
        }
        ((MainActivity)requireActivity()).getDeleteButton().setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext()).setMessage("Are you sure you want to delete " + selectionTracker.getSelection().size() + " entries?")
                    .setCancelable(true)
                    .setPositiveButton("Yes, I'm Sure", (dialog, which) -> {
                        selectionTracker.getSelection().forEach(aLong -> {
                            if(aLong >= DBManager.IncomeOffset){
                                DBManager.getDBManagerInstance().deleteIncomeEntries(aLong);
                            } else {
                                DBManager.getDBManagerInstance().deleteExpenseEntries(aLong);
                            }
                        });
                        selectionTracker.clearSelection();
                        refreshEntries();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}