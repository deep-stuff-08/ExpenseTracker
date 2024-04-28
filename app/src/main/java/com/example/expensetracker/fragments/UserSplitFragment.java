package com.example.expensetracker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.Settings;
import com.example.expensetracker.adapters.UserSplitAdapter;

public class UserSplitFragment extends Fragment {
    public UserSplitFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)requireActivity()).getDeleteButton().setVisibility(View.GONE);
        Settings settings = ((MainActivity)requireActivity()).getSettings();

        View view = inflater.inflate(R.layout.fragment_user_split, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.user_list);
        recyclerView.setAdapter(new UserSplitAdapter(settings.getUsers(), u -> {
        }));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setVerticalScrollbarPosition(0);
        return view;
    }
}