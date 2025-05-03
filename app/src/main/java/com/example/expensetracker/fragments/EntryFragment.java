package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.service.autofill.DateValueSanitizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.TabLayoutAdapter;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.TransferData;
import com.example.expensetracker.pojo.UnconfirmedEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EntryFragment extends Fragment {
    TabLayoutAdapter tabLayoutAdapter;
    ViewPager2 viewPager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity)requireActivity()).getDeleteButton().setVisibility(View.GONE);
        return inflater.inflate(R.layout.fragment_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        UnconfirmedEntry unconfirmedEntry = null;
        TransferData transferData = null;
        Bundle arg = getArguments();
        final long unconfirmedEntryId = arg == null ? -1 : arg.getLong("unconfirmedEntryId", -1);
        final long transferUserId = arg == null ? -1 : arg.getLong("TransferUserId", -1);
        if(unconfirmedEntryId >= 0) {
            unconfirmedEntry = DBManager.getDBManagerInstance().getUnconfirmedEntry(unconfirmedEntryId);
        } else if(transferUserId >= 0) {
            int value = arg.getInt("TransferValue", 0);
            transferData = new TransferData(transferUserId, value);
        }

        tabLayoutAdapter = new TabLayoutAdapter(this, unconfirmedEntry, transferData);
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
        } else if(transferUserId >= 0) {
            tabLayout.selectTab(tabLayout.getTabAt(2));
            viewPager.setCurrentItem(2);
        }
    }
}
