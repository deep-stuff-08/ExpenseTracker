package com.example.expensetracker.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.TransferUserAdapter;
import com.example.expensetracker.pojo.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class TransferFragment extends Fragment {
    public TransferFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer, container, false);

        Spinner spinnerTo = view.findViewById(R.id.spinner_to);
        Spinner spinnerFrom = view.findViewById(R.id.spinner_from);
        ImageButton btnSwap = view.findViewById(R.id.btn_swap);
        TextView txtValue = view.findViewById(R.id.edit_value);

        ArrayList<String> users = new ArrayList<>();
        ((MainActivity)requireActivity()).getSettings().getUsers().forEach(user -> users.add(user.getName()));
        users.add("Me");

        spinnerTo.setAdapter(new TransferUserAdapter(requireContext(), users));
        spinnerFrom.setAdapter(new TransferUserAdapter(requireContext(), users));

        return view;
    }
}