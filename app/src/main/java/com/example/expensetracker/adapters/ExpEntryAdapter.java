package com.example.expensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Entry;

import java.util.ArrayList;
import java.util.Locale;

public class ExpEntryAdapter extends RecyclerView.Adapter<ExpEntryAdapter.ViewHolder> {
    ArrayList<Entry> entries;
    MainActivity activity;
    public ExpEntryAdapter(MainActivity activity, ArrayList<Entry> entries) {
        this.activity = activity;
        this.entries = entries;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_expenses, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(entries.get(position).getName());
        holder.payment.setText(String.format(Locale.getDefault(), "%d", entries.get(position).getPaymentId()));
    }
    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView payment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.expense_name);
            payment = itemView.findViewById(R.id.payment_type);
        }
    }
}
