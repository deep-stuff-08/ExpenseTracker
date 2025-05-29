package com.example.expensetracker.fragments;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.SmsReader;
import com.example.expensetracker.adapters.UnconfirmedEntriesAdapter;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UnconfirmedEntriesFragment extends Fragment {
    private View.OnClickListener recheckClickListener;
    private final CharSequence recheckText = "Recheck";
    private View.OnClickListener discardClickListener;
    private final CharSequence discardText = "Discard All";
    private RecyclerView recyclerView;
    private RecyclerView.AdapterDataObserver dataObserver;
    private Button discardBtn;
    private TextView noEntriesTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unconfirmed_entries, container, false);

        recyclerView = view.findViewById(R.id.unconfirmed_recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setVerticalScrollbarPosition(0);
        discardBtn = view.findViewById(R.id.discard_all_btn);
        noEntriesTxt = view.findViewById(R.id.text_no_entries);

        dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if(recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == 0) {
                    Init();
                }
            }
        };

        recheckClickListener = v -> {
            View datepickview = View.inflate(view.getContext(), R.layout.dialog_datepicker, null);
            DatePicker datePicker = datepickview.findViewById(R.id.recheck_dateview);
            datePicker.setMaxDate(new Date().getTime());
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Select Date")
                    .setView(datepickview)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), 0, 0, 0);
                        ArrayList<UnconfirmedEntry> entries = new SmsReader().readMessagesSentAfter(requireContext(), calendar.getTime());
                        if(entries.isEmpty()) {
                            Toast.makeText(getContext(), "Sorry, No messages found.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        entries.forEach(entry -> DBManager.getDBManagerInstance().insertUnconfirmedEntries(entry));
                        Init();
                    })
                    .setCancelable(true)
                    .show();
        };

        discardClickListener = v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to discard all entries ?")
                    .setPositiveButton("Yes", (dialog, which) ->
                    {
                        if(recyclerView.getAdapter() != null) {
                            ((UnconfirmedEntriesAdapter)recyclerView.getAdapter()).clearAll();
                            DBManager.getDBManagerInstance().deleteAllUnconfirmedEntries();
                        }
                        Init();
                    })
                    .setNegativeButton("No", null)
                    .show();
        };

        Init();

        return view;
    }

    void Init() {
        ArrayList<UnconfirmedEntry> list = DBManager.getDBManagerInstance().getUnconfirmedEntries();
        UnconfirmedEntriesAdapter adapter = new UnconfirmedEntriesAdapter(list);
        adapter.registerAdapterDataObserver(dataObserver);
        recyclerView.setAdapter(adapter);
        if(list.size() == 0) {
            SetupButtonAsRecheck();
        } else {
            SetupButtonAsDiscardAll();
        }
    }

    void SetupButtonAsDiscardAll() {
        discardBtn.setOnClickListener(discardClickListener);
        discardBtn.setText(discardText);
        noEntriesTxt.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    void SetupButtonAsRecheck() {
        discardBtn.setOnClickListener(recheckClickListener);
        discardBtn.setText(recheckText);
        noEntriesTxt.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}