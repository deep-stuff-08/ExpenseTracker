package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.adapters.ExpEntryAdapter;
import com.example.expensetracker.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View cview = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recycler = cview.findViewById(R.id.recycleView);
        recycler.setAdapter(new ExpEntryAdapter());

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setVerticalScrollbarPosition(0);

        return cview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        view.findViewById(R.id.fab).setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_homeFragment_to_addExpenseFragment));
        view.findViewById(R.id.unconfirmed_parent).setOnClickListener(view2 -> Navigation.findNavController(view2).navigate(R.id.action_homeFragment_to_unconfirmedEntryFragment));
    }

    public void setUnconfirmedVisible() {
        getView().findViewById(R.id.unconfirmed_parent).setVisibility(View.VISIBLE);
    }
}