package com.example.expensetracker.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.expensetracker.fragments.AddExpenseFragment;
import com.example.expensetracker.fragments.TransferFragment;
import com.example.expensetracker.pojo.UnconfirmedEntry;

public class TabLayoutAdapter extends FragmentStateAdapter {
    UnconfirmedEntry entry;
    public TabLayoutAdapter(@NonNull Fragment fragment, UnconfirmedEntry entry) {
        super(fragment);
        this.entry = entry;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new AddExpenseFragment(false, entry);
            case 1:
                return new AddExpenseFragment(true, entry);
            case 2:
                return new TransferFragment();
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
