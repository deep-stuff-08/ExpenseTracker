package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsModifyAdapter extends RecyclerView.Adapter<SettingsModifyAdapter.SettingsModifyViewHolder> {
    private final ArrayList<ExpenseSettings.LogoNameCombo> settingsData;

    public SettingsModifyAdapter(ArrayList<ExpenseSettings.LogoNameCombo> data) {
        settingsData = data;
    }
    @NonNull
    @Override
    public SettingsModifyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modify_methods, parent, false);
        return new SettingsModifyAdapter.SettingsModifyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsModifyViewHolder holder, int position) {
        holder.textview.setText(settingsData.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return settingsData.size();
    }

    public static class SettingsModifyViewHolder extends  RecyclerView.ViewHolder {
        private final TextView textview;
        public SettingsModifyViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.modify_text);
        }
    }
}
