package com.example.expensetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.POJO.Category;
import com.example.expensetracker.POJO.PaymentType;
import com.example.expensetracker.POJO.SubCategory;

import java.util.ArrayList;

public class SettingsEntryAdapter extends RecyclerView.Adapter<SettingsEntryAdapter.ViewHolder> {
    private int mExpandedPosition = -1;
    private final ArrayList<String> dataList;

    private Context currentContext;

    public SettingsEntryAdapter() {
        dataList = new ArrayList<>();
        dataList.add("Edit Payment Method");
        dataList.add("Edit Category");
        dataList.add("Edit Sub Category");
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
        holder.hiddenCategory.setVisibility(isExpanded && position == 2?View.VISIBLE:View.GONE);
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
        private final Spinner hiddenCategory;
        private final Button hiddenButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textview = itemView.findViewById(R.id.settings_category_text);
            hidden = itemView.findViewById(R.id.settings_hidden);
            hiddenCategory = itemView.findViewById(R.id.settings_hidden_category);
            hiddenButton = itemView.findViewById(R.id.settings_hidden2);
        }
    }

    private void setupListeners(ViewHolder holder, int rootPosition) {
       ExpenseSettings settings = ((MainActivity)currentContext).getSettings();
       holder.hiddenCategory.setAdapter(new SpinnerCategoryAdapter(currentContext, settings.getCategory(), 0));
       SettingsUpdateListener nameListener = new SettingsUpdateListener() {
            @Override
            public void onSettingsNameUpdateListener(int position, String newName) {
                switch(rootPosition) {
                    case 0:
                        settings.updatePaymentMethodName(position, newName);
                        break;
                    case 1:
                        settings.updateCategoryName(position, newName);
                        break;
                    case 2:
                        settings.updateSubCategoryName(holder.hiddenCategory.getSelectedItemPosition(), position, newName);
                        break;
                }
                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemChanged(position);
            }

            @Override
            public void onSettingsLogoUpdateListener(int position, int newLogo) {
                switch (rootPosition) {
                    case 0:
                        settings.updatePaymentMethodDrawId(position, newLogo);
                        break;
                    case 1:
                        settings.updateCategoryColorId(position, newLogo);
                        break;
                    case 2:
                        settings.updateSubCategoryDrawId(holder.hiddenCategory.getSelectedItemPosition(), position, newLogo);
                        break;
                }
            }

            @Override
            public void onSettingsDeleteListener(int position) {
                switch (rootPosition) {
                    case 0:
                        settings.deletePaymentMethod(position);
                        break;
                    case 1:
                        settings.deleteCategory(position);
                        break;
                    case 2:
                        settings.deleteSubCategory(holder.hiddenCategory.getSelectedItemPosition(), position);
                        break;
                }
                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemRemoved(position);
            }

            @Override
            public void onSettingsAddListener(String name, int logo) {
                switch (rootPosition) {
                    case 0:
                        settings.addPaymentMethod(new PaymentType(name, logo));
                        break;
                    case 1:
                        settings.addCategory(new Category(name, logo, new ArrayList<>()));
                        break;
                    case 2:
                        settings.addSubCategory(holder.hiddenCategory.getSelectedItemPosition(), new SubCategory(name, logo));
                        break;
                }
                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemInserted(holder.hidden.getAdapter().getItemCount() + 1);
            }
        };
        SettingsModifyAdapter settingsModifyAdapter;
        switch (rootPosition) {
            case 0:
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getPaymentMethod(), nameListener);
                break;
            case 1:
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getCategory(), nameListener);
                break;
            case 2:
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getSubCategory(0), nameListener);
                break;
            default:
                settingsModifyAdapter = new SettingsModifyAdapter(new ArrayList<>(), null);
                break;
        }
        holder.hidden.setAdapter(settingsModifyAdapter);
        holder.hidden.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.hidden.setVerticalScrollbarPosition(0);

        holder.hiddenCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(rootPosition == 2) {
                    holder.hidden.setAdapter(new SettingsModifyAdapter(settings.getSubCategory(position), nameListener));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.hiddenButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            View diagView = View.inflate(holder.itemView.getContext(), R.layout.edittext_dialog, null);
            builder.setView(diagView);
            builder.setTitle("Add New Item");
            builder.setPositiveButton("Add", (dialogInterface, i) -> {
                nameListener.onSettingsAddListener(((EditText)diagView.findViewById(R.id.dialog_edittext)).getText().toString(), R.drawable.ic_launcher_foreground);
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}
