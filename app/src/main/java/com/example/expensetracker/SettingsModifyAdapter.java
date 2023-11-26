package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsModifyAdapter extends RecyclerView.Adapter<SettingsModifyAdapter.SettingsModifyViewHolder> {
    private final ArrayList<ExpenseSettings.LogoNameCombo> settingsData;
    private final ExpenseSettings.SettingsUpdateListener updateListener;

    public SettingsModifyAdapter(ArrayList<ExpenseSettings.LogoNameCombo> data, ExpenseSettings.SettingsUpdateListener listener) {
        settingsData = data;
        updateListener = listener;
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
        holder.modifyBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            View diagview = View.inflate(view.getContext(), R.layout.edittext_dialog, null);
            builder.setView(diagview);
            builder.setTitle("Modify");
            builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                EditText editText = diagview.findViewById(R.id.dialog_edittext);
                String newName = editText.getText().toString();
                if(!newName.equals(settingsData.get(holder.getAdapterPosition()).getName())) {
                    updateListener.onSettingsNameUpdateListener(holder.getAdapterPosition(), newName);
                    updateListener.onSettingsLogoUpdateListener(holder.getAdapterPosition(), R.drawable.ic_launcher_foreground);
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            EditText editText = diagview.findViewById(R.id.dialog_edittext);
            editText.setText(settingsData.get(holder.getAdapterPosition()).getName());
            dialog.show();
        });
        holder.removeBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete '"+settingsData.get(holder.getAdapterPosition()).getName()+"'?");
            builder.setPositiveButton("Yes", (dialogInterface, i) -> updateListener.onSettingsNameLogoComboDeleteListener(holder.getAdapterPosition()));
            builder.setNegativeButton("No", null);
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return settingsData.size();
    }

    public static class SettingsModifyViewHolder extends  RecyclerView.ViewHolder {
        private final TextView textview;
        private final ImageButton modifyBtn;
        private final ImageButton removeBtn;
        public SettingsModifyViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.modify_text);
            modifyBtn = itemView.findViewById(R.id.modify_modify_btn);
            removeBtn = itemView.findViewById(R.id.modify_remove_btn);
        }
    }
}
