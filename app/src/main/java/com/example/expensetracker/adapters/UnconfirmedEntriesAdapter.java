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

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.util.ArrayList;

public class UnconfirmedEntriesAdapter extends RecyclerView.Adapter<UnconfirmedEntriesAdapter.ViewHolder> {
    private final ArrayList<UnconfirmedEntry> entries;
    private int mExpandedPosition = -1;

    public UnconfirmedEntriesAdapter(ArrayList<UnconfirmedEntry> newEntries) {
        entries = newEntries;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_unconfirmed, parent, false);
        return new UnconfirmedEntriesAdapter.ViewHolder(view);
    }

    @NonNull
    public void clearAll() {
        int size = entries.size();
        entries.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.title.setText(entries.get(position).getSender());
        holder.date.setText(MainActivity.userFriendlyDateFormatter.format(entries.get(position).getSentDate()));
        holder.value.setText(String.valueOf(entries.get(position).getValue()));
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(mExpandedPosition);
            mExpandedPosition = isExpanded ? -1:holder.getAbsoluteAdapterPosition();
            notifyItemChanged(holder.getAbsoluteAdapterPosition());
        });
        holder.delete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.delete.setOnClickListener(v -> {
            long entryID = entries.get(holder.getAbsoluteAdapterPosition()).getId();
            entries.remove(holder.getAbsoluteAdapterPosition());
            DBManager.getDBManagerInstance().deleteUnconfirmedEntries(entryID);
            notifyItemRemoved(holder.getAbsoluteAdapterPosition());
        });
        holder.confirm.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.confirm.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("unconfirmedEntryId", entries.get(holder.getAbsoluteAdapterPosition()).getId());
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
