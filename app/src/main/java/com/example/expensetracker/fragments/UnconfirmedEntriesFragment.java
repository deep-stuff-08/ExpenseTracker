package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.UnconfirmedEntriesAdapter;

public class UnconfirmedEntriesFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)requireActivity()).getDeleteButton().setVisibility(View.GONE);
        View view = inflater.inflate(R.layout.fragment_unconfirmed_entries, container, false);

        RecyclerView recycler = view.findViewById(R.id.unconfirmed_recycleView);
        recycler.setAdapter(new UnconfirmedEntriesAdapter(((MainActivity)requireActivity()).getEntries()));

        recycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recycler.setVerticalScrollbarPosition(0);

        return view;
    }
}