package com.example.expensetracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.expensetracker.fragments.AddExpenseFragment;
import com.example.expensetracker.fragments.TransferFragment;

public class TabLayoutAdapter extends FragmentStateAdapter {
    public TabLayoutAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new AddExpenseFragment();
        } else {
            return new TransferFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
