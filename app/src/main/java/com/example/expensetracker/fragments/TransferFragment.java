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
import android.widget.SpinnerAdapter;
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
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        ArrayList<String> me = new ArrayList<>();
        ((MainActivity)requireActivity()).getSettings().getPaymentMethod().forEach(type -> me.add("Me (" + type.getName() + ")"));
        ArrayList<String> users = new ArrayList<>();
        ((MainActivity)requireActivity()).getSettings().getUsers().forEach(user -> users.add(user.getName()));

        TransferUserAdapter combinedUserTo = new TransferUserAdapter(me, users);
        TransferUserAdapter combinedUserFrom = new TransferUserAdapter(me, users);

        spinnerTo.setAdapter(combinedUserTo);
        spinnerFrom.setAdapter(combinedUserFrom);

        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TransferUserAdapter)spinnerFrom.getAdapter()).setEnableUsers(id == 0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TransferUserAdapter)spinnerTo.getAdapter()).setEnableUsers(id == 0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSwap.setOnClickListener(v -> {
            boolean from = ((TransferUserAdapter)spinnerFrom.getAdapter()).getEnableUsers();
            ((TransferUserAdapter)spinnerFrom.getAdapter()).setEnableUsers(((TransferUserAdapter)spinnerTo.getAdapter()).getEnableUsers());
            ((TransferUserAdapter)spinnerTo.getAdapter()).setEnableUsers(from);

            int froms = spinnerFrom.getSelectedItemPosition();
            spinnerFrom.setSelection(spinnerTo.getSelectedItemPosition());
            spinnerTo.setSelection(froms);
        });

        return view;
    }
}