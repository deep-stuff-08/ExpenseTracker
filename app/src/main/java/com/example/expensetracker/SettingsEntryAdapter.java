package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import java.util.ArrayList;

public class SettingsEntryAdapter extends RecyclerView.Adapter<SettingsEntryAdapter.ViewHolder> {
    private int mExpandedPosition = -1;
    private final ArrayList<String> dataList;

    public SettingsEntryAdapter() {
        dataList = new ArrayList<>();
        dataList.add("Edit Payment Method");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_entry, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.hidden.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.hiddenButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            mExpandedPosition = isExpanded ? -1:holder.getAdapterPosition();
            //TransitionManager.beginDelayedTransition(recyclerView);
            notifyItemChanged(position);
        });
        holder.textview.setText(dataList.get(position));
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textview;
        private final RecyclerView hidden;
        private final Button hiddenButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.settings_category_text);
            hidden = itemView.findViewById(R.id.settings_hidden);
            SettingsModifyAdapter settingsModifyAdapter = new SettingsModifyAdapter(((MainActivity) itemView.getContext()).getSettings().getPaymentMethod());
            hidden.setAdapter(settingsModifyAdapter);
            hidden.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            hidden.setVerticalScrollbarPosition(0);
            hiddenButton = itemView.findViewById(R.id.settings_hidden2);
        }
    }
}
