package com.example.expensetracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
            ExpenseSettings settings = ((MainActivity) itemView.getContext()).getSettings();
            ExpenseSettings.SettingsUpdateListener nameListener = new ExpenseSettings.SettingsUpdateListener() {
                @Override
                public void onSettingsNameUpdateListener(int position, String newName) {
                    settings.updatePaymentMethodName(position, newName);
                    if(hidden.getAdapter() != null)
                        hidden.getAdapter().notifyItemChanged(position);
                }

                @Override
                public void onSettingsLogoUpdateListener(int position, int newLogo) {
                    settings.updatePaymentMethodLogo(position, newLogo);
                }

                @Override
                public void onSettingsNameLogoComboDeleteListener(int position) {
                    settings.deletePaymentMethod(position);
                    if(hidden.getAdapter() != null)
                        hidden.getAdapter().notifyItemRemoved(position);
                }

                @Override
                public void onSettingsNameLogoComboAddListener(String name, int logo) {
                    settings.addPaymentMethod(new ExpenseSettings.LogoNameCombo(name, logo));
                    if(hidden.getAdapter() != null)
                        hidden.getAdapter().notifyItemInserted(hidden.getAdapter().getItemCount() + 1);
                }
            };
            SettingsModifyAdapter settingsModifyAdapter = new SettingsModifyAdapter(settings.getPaymentMethod(), nameListener);
            hidden.setAdapter(settingsModifyAdapter);
            hidden.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            hidden.setVerticalScrollbarPosition(0);
            hiddenButton = itemView.findViewById(R.id.settings_hidden2);
            hiddenButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                View diagView = View.inflate(itemView.getContext(), R.layout.edittext_dialog, null);
                builder.setView(diagView);
                builder.setTitle("Add New Item");
                builder.setPositiveButton("Add", (dialogInterface, i) -> nameListener.onSettingsNameLogoComboAddListener(((EditText)diagView.findViewById(R.id.dialog_edittext)).getText().toString(), R.drawable.ic_launcher_foreground));
                builder.setNegativeButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
    }
}
