package com.example.expensetracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

public class AddExpenseFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView expenseName = view.findViewById(R.id.expense_name);
        ArrayAdapter<String> adt = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, new String[]{"Deep", "Fee"});
        expenseName.setAdapter(adt);
        expenseName.setThreshold(1);

        Spinner expenseCategory = view.findViewById(R.id.expense_category);
        SpinnerCategoryAdapter sadt = new SpinnerCategoryAdapter(getContext(), new SpinnerCategoryAdapter.CategoryItem[]{new SpinnerCategoryAdapter.CategoryItem("Next", android.R.drawable.ic_media_next)});
        expenseCategory.setAdapter(sadt);

        Spinner expenseSubCategory = view.findViewById(R.id.expense_subcategory);
        SpinnerCategoryAdapter sadt2 = new SpinnerCategoryAdapter(getContext(), new SpinnerCategoryAdapter.CategoryItem[]{new SpinnerCategoryAdapter.CategoryItem("Prev", android.R.drawable.ic_media_previous)});
        expenseSubCategory.setAdapter(sadt2);
    }
}