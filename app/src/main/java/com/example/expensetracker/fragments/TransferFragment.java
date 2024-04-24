package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.adapters.TransferUserAdapter;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.User;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class TransferFragment extends Fragment {
    public TransferFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((MainActivity)requireActivity()).getDeleteButton().setVisibility(View.GONE);
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
                TransferUserAdapter t = ((TransferUserAdapter)spinnerFrom.getAdapter());
                if(position != 0) {
                    String item = (String) spinnerTo.getSelectedItem();
                    t.setSelectedItem(item);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TransferUserAdapter t = ((TransferUserAdapter) spinnerTo.getAdapter());
                if(position != 0) {
                    String item = (String) spinnerFrom.getSelectedItem();
                    t.setSelectedItem(item);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSwap.setOnClickListener(v -> {
            TransferUserAdapter from = (TransferUserAdapter) spinnerFrom.getAdapter();
            TransferUserAdapter to = (TransferUserAdapter) spinnerTo.getAdapter();

            TransferUserAdapter.State fromState = from.getState();
            TransferUserAdapter.State toState = to.getState();

            from.resetState();
            to.resetState();

            int selected = from.getItemPosition((String)spinnerFrom.getSelectedItem());
            int selected2 = to.getItemPosition((String)spinnerTo.getSelectedItem());
            spinnerFrom.setSelection(selected2);
            spinnerTo.setSelection(selected);

            from.setState(toState);
            to.setState(fromState);
        });

        btnSubmit.setOnClickListener(v -> {
            String from = (String)spinnerFrom.getSelectedItem();
            String to = (String)spinnerTo.getSelectedItem();
            String value = txtValue.getText().toString();

            boolean error = false;

            if(Objects.equals(from, "Select an Option")) {
                ((TextView)spinnerFrom.getSelectedView().findViewById(R.id.spinner_category_text)).setError("From Not Set");
                error = true;
            }
            if(Objects.equals(to, "Select an Option")) {
                ((TextView)spinnerTo.getSelectedView().findViewById(R.id.spinner_category_text)).setError("To Not Set");
                error = true;
            }

            if(value.isEmpty()) {
                txtValue.setError("Value Empty");
                error = true;
            }

            long fromUserId = -1;
            long toUserId = -1;
            for(User u : ((MainActivity)requireActivity()).getSettings().getUsers()) {
                if(fromUserId == -1 && Objects.equals(u.getName(), from)) {
                    fromUserId = u.getId();
                }
                if(toUserId == -1 && Objects.equals(u.getName(), to)) {
                    toUserId = u.getId();
                }
            }

            if(toUserId != -1 && fromUserId != -1) {
                ((TextView)spinnerFrom.getSelectedView().findViewById(R.id.spinner_category_text)).setError("From Not Set");
                ((TextView)spinnerTo.getSelectedView().findViewById(R.id.spinner_category_text)).setError("To Not Set");
                error = true;
            }

            if(error) {
                return;
            }
            DBManager.getDBManagerInstance().insertTransferEntries(fromUserId, toUserId, Integer.parseInt(value));

            Snackbar.make(view, "Entry Saved Successfully", Snackbar.LENGTH_SHORT).show();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        return view;
    }
}