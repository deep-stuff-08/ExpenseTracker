package com.example.expensetracker.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class UnconfirmedEntriesAdapter extends RecyclerView.Adapter<UnconfirmedEntriesAdapter.ViewHolder> {
    private final ArrayList<UnconfirmedEntry> entries;
    private final SimpleDateFormat sdf;
    private int mExpandedPosition = -1;

    public UnconfirmedEntriesAdapter(ArrayList<UnconfirmedEntry> newEntries) {
        entries = newEntries;
        sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss", Locale.getDefault());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_unconfirmed, parent, false);
        return new UnconfirmedEntriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.title.setText(entries.get(position).getSender());
        holder.date.setText(sdf.format(entries.get(position).getSentDate()));
        holder.value.setText(String.valueOf(entries.get(position).getValue()));
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(mExpandedPosition);
            mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
            notifyItemChanged(holder.getAdapterPosition());
        });
        holder.delete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.delete.setOnClickListener(v -> {
            entries.remove(holder.getAdapterPosition());
            notifyItemRemoved(holder.getAdapterPosition());
        });
        holder.confirm.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.confirm.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("unconfirmedEntryId", holder.getAdapterPosition());
            Navigation.findNavController(holder.itemView).navigate(R.id.action_unconfirmedEntryFragment_to_entryFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView date;
        private final TextView value;

        private final Button delete;
        private final Button confirm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.unconfirmed_title);
            date = itemView.findViewById(R.id.unconfirmed_date);
            value = itemView.findViewById(R.id.unconfirmed_value);
            delete = itemView.findViewById(R.id.unconfirmed_deleteBtn);
            confirm = itemView.findViewById(R.id.unconfirmed_confirmBtn);
        }
    }
}
