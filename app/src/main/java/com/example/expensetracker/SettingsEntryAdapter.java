package com.example.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SettingsEntryAdapter extends RecyclerView.Adapter<SettingsEntryAdapter.ViewHolder> {
    private int mExpandedPosition = -1;
    private final ArrayList<String> dataList;

    private Context currentContext;

    public SettingsEntryAdapter() {
        dataList = new ArrayList<>();
        dataList.add("Edit Payment Method");
        dataList.add("Edit Category");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_entry, parent, false);
        currentContext = parent.getContext();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.hidden.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.hiddenButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(mExpandedPosition);
            mExpandedPosition = isExpanded ? -1:position;
            notifyItemChanged(position);
        });
        holder.textview.setText(dataList.get(position));
        if(isExpanded) {
            setupListeners(holder, position);
        }
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
            hiddenButton = itemView.findViewById(R.id.settings_hidden2);
        }
    }

    private void setupListeners(ViewHolder holder, int rootposition) {
        ExpenseSettings settings = ((MainActivity)currentContext).getSettings();
        ExpenseSettings.SettingsUpdateListener nameListener = new ExpenseSettings.SettingsUpdateListener() {
            @Override
            public void onSettingsNameUpdateListener(int position, String newName) {
                switch(rootposition) {
                    case 0:
                        settings.updatePaymentMethodName(position, newName);
                        break;
                    case 1:
                        settings.updateCategoryName(position, newName);
                        break;
                }
                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemChanged(position);
            }

            @Override
            public void onSettingsLogoUpdateListener(int position, int newLogo) {
               switch (rootposition) {
                   case 0:
                       settings.updatePaymentMethodLogo(position, newLogo);
                       break;
                   case 1:
                       settings.updateCategoryLogo(position, newLogo);
                       break;
               }
            }

            @Override
            public void onSettingsNameLogoComboDeleteListener(int position) {
                switch (rootposition) {
                    case 0:
                        settings.deletePaymentMethod(position);
                        break;
                    case 1:
                        settings.deleteCategory(position);
                        break;
                }
                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemRemoved(position);
            }

            @Override
            public void onSettingsNameLogoComboAddListener(String name, int logo) {
                switch (rootposition) {
                    case 0:
                        settings.addPaymentMethod(new ExpenseSettings.LogoNameCombo(name, logo));
                        break;
                    case 1:
                        settings.addCategory(new ExpenseSettings.LogoNameCombo(name, logo));
                        break;
                }
                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemInserted(holder.hidden.getAdapter().getItemCount() + 1);
            }
        };

        SettingsModifyAdapter settingsModifyAdapter;
        switch (rootposition) {
            case 0:
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getPaymentMethod(), nameListener);
                break;
            case 1:
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getCategory(), nameListener);
                break;
            default:
                settingsModifyAdapter = new SettingsModifyAdapter(new ArrayList<>(), null);
                break;
        }
        holder.hidden.setAdapter(settingsModifyAdapter);
        holder.hidden.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.hidden.setVerticalScrollbarPosition(0);

        holder.hiddenButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            View diagView = View.inflate(holder.itemView.getContext(), R.layout.edittext_dialog, null);
            builder.setView(diagView);
            builder.setTitle("Add New Item");
            builder.setPositiveButton("Add", (dialogInterface, i) -> nameListener.onSettingsNameLogoComboAddListener(((EditText)diagView.findViewById(R.id.dialog_edittext)).getText().toString(), R.drawable.ic_launcher_foreground));
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}
