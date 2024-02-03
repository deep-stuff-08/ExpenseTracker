package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.TabLayoutAdapter;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EntryFragment extends Fragment {
    TabLayoutAdapter tabLayoutAdapter;
    ViewPager2 viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        UnconfirmedEntry unconfirmedEntry = null;
        Bundle arg = getArguments();
        final int unconfirmedEntryId = arg == null ? -1 : arg.getInt("unconfirmedEntryId", -1);
        if(unconfirmedEntryId >= 0) {
            unconfirmedEntry = ((MainActivity) requireActivity()).getEntries().get(unconfirmedEntryId);
        }

        tabLayoutAdapter = new TabLayoutAdapter(this, unconfirmedEntry);
        viewPager = view.findViewById(R.id.entry_view_pager);
        viewPager.setAdapter(tabLayoutAdapter);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch(position) {
                case 0:
                    tab.setText("Expense");
                    break;
                case 1:
                    tab.setText("Income");
                    break;
                case 2:
                    tab.setText("Transfer");
                    break;
            }
        }).attach();

        if(unconfirmedEntry != null) {
            tabLayout.selectTab(tabLayout.getTabAt(unconfirmedEntry.isCredited() ? 1 : 0));
            viewPager.setCurrentItem(unconfirmedEntry.isCredited() ? 1 : 0);
        }
    }
}
