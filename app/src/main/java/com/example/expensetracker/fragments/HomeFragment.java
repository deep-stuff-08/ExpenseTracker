package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.ExpEntryAdapter;
import com.example.expensetracker.database.DBManager;

public class HomeFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recycler = view.findViewById(R.id.recycleView);
        recycler.setAdapter(new ExpEntryAdapter((MainActivity) getActivity(), DBManager.getDBManagerInstance().getExpenseEntries()));

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setVerticalScrollbarPosition(0);

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
        if(((MainActivity)requireActivity()).getEntries().size() > 0) {
            requireView().findViewById(R.id.unconfirmed_parent).setVisibility(View.VISIBLE);
        }
    }
}