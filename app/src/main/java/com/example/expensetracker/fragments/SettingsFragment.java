package com.example.expensetracker.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensetracker.adapters.SettingsEntryAdapter;
import com.example.expensetracker.R;

public class SettingsFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        RecyclerView recycler = view.findViewById(R.id.settings_recycle_view);
        recycler.setAdapter(new SettingsEntryAdapter());
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setVerticalScrollbarPosition(0);

        return view;
    }
}