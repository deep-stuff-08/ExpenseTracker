package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.UnconfirmedEntriesAdapter;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.util.ArrayList;

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
        ArrayList<UnconfirmedEntry> list = DBManager.getDBManagerInstance().getUnconfirmedEntries();
        recycler.setAdapter(new UnconfirmedEntriesAdapter(list));

        recycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recycler.setVerticalScrollbarPosition(0);
        if(list.size() == 0) {
            recycler.setVisibility(View.GONE);
            view.findViewById(R.id.text_no_entries).setVisibility(View.VISIBLE);
        }

        Button button = view.findViewById(R.id.discard_all_btn);
        button.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to discard all entries ?")
                    .setPositiveButton("Yes", (dialog, which) ->
                    {
                        if(recycler.getAdapter() != null) {
                            ((UnconfirmedEntriesAdapter)recycler.getAdapter()).clearAll();
                            DBManager.getDBManagerInstance().deleteAllUnconfirmedEntries();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return view;
    }
}