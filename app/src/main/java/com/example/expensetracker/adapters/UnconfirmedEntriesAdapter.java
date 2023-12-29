package com.example.expensetracker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.Inflater;

public class UnconfirmedEntriesAdapter extends RecyclerView.Adapter<UnconfirmedEntriesAdapter.ViewHolder> {
    private ArrayList<UnconfirmedEntry> entries;
    private SimpleDateFormat sdf;
    private int mExpandedPosition = -1;

    public UnconfirmedEntriesAdapter(ArrayList<UnconfirmedEntry> newentries) {
        entries = newentries;
        sdf = new SimpleDateFormat("dd/MM/yy hh:mm:ss", Locale.getDefault());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unconfirmed_entries, parent, false);
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
            mExpandedPosition = isExpanded ? -1:position;
            notifyItemChanged(position);
        });
        holder.delete.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.confirm.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
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
