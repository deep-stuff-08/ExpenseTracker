package com.example.expensetracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.Settings;
import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.SettingsUpdateListener;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;

public class SettingsEntryAdapter extends RecyclerView.Adapter<SettingsEntryAdapter.ViewHolder> {
    private int mExpandedPosition = -1;
    private final ArrayList<String> dataList;

    private Context currentContext;

    public SettingsEntryAdapter() {
        dataList = new ArrayList<>();
        dataList.add("Edit Payment Method");
        dataList.add("Edit Expense Categories");
        dataList.add("Edit Expense Sub-Categories");
        dataList.add("Edit Income Categories");
        dataList.add("Edit Income Sub-Categories");
        dataList.add("Edit Users");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_settings, parent, false);
        currentContext = parent.getContext();
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final boolean isExpanded = position==mExpandedPosition;
        holder.hidden.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.hiddenButton.setVisibility(isExpanded?View.VISIBLE:View.GONE);
        holder.hiddenCategory.setVisibility(isExpanded && (position == 2 || position == 4)?View.VISIBLE:View.GONE);
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

    private void updateNewExpenseSetting()
    {
        Settings settings = ((MainActivity)currentContext).getSettings();
        Settings newSettings = Settings.createWithParametersFromDatabase();
        settings.updateExpenseSettings(newSettings);
    }

    private void setupListeners(ViewHolder holder, int rootPosition) {
       Settings settings = ((MainActivity)currentContext).getSettings();
       SettingsUpdateListener nameListener = new SettingsUpdateListener() {
            @Override
            public void onSettingsUpdateListener(int position, String newName, int newLogo) {
                switch(rootPosition) {
                    case 0: //Payment Type
                        PaymentType p = settings.getPaymentMethod().get(position);
                        p.setName(newName);
                        p.setDrawableId(newLogo);
                        DBManager.getDBManagerInstance().updatePaymentType(p);
                        break;
                    case 1: //Expense Category
                        Category ec = settings.getExpenseCategory().get(position);
                        ec.setName(newName);
                        ec.setColorId(newLogo);
                        DBManager.getDBManagerInstance().updateExpenseCategory(ec);
                        break;
                    case 2: //Expense Sub-Category
                        SubCategory es = settings.getExpenseCategory().get(holder.hiddenCategory.getSelectedItemPosition()).getSubCategories().get(position);
                        es.setName(newName);
                        es.setDrawableId(newLogo);
                        DBManager.getDBManagerInstance().updateExpenseSubCategory(es);
                        break;
                    case 3: //Income Category
                        Category ic = settings.getIncomeCategory().get(position);
                        ic.setName(newName);
                        ic.setColorId(newLogo);
                        DBManager.getDBManagerInstance().updateIncomeCategory(ic);
                        break;
                    case 4: //Income Sub-Category
                        SubCategory is = settings.getIncomeCategory().get(holder.hiddenCategory.getSelectedItemPosition()).getSubCategories().get(position);
                        is.setName(newName);
                        is.setDrawableId(newLogo);
                        DBManager.getDBManagerInstance().updateIncomeSubCategory(is);
                        break;
                    case 5: //Users
                        User u = settings.getUsers().get(position);
                        u.setName(newName);
                        u.setLogo(newLogo);
                        DBManager.getDBManagerInstance().updateUser(u);
                }

                updateNewExpenseSetting();

                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemChanged(position);
            }


            @Override
            public void onSettingsDeleteListener(int position) {
                switch (rootPosition) {
                    case 0: //Payment Type
                        DBManager.getDBManagerInstance().deletePaymentType(settings.getPaymentMethod().get(position));
                        break;
                    case 1: //Expense Category
                        DBManager.getDBManagerInstance().deleteExpenseCategory(settings.getExpenseCategory().get(position));
                        break;
                    case 2: //Expense Sub-Category
                        DBManager.getDBManagerInstance().deleteExpenseSubCategory(settings.getExpenseCategory().get(holder.hiddenCategory.getSelectedItemPosition()).getSubCategories().get(position));
                        break;
                    case 3: //Income Category
                        DBManager.getDBManagerInstance().deleteIncomeCategory(settings.getIncomeCategory().get(position));
                        break;
                    case 4: //Income Sub-Category
                        DBManager.getDBManagerInstance().deleteIncomeSubCategory(settings.getIncomeCategory().get(holder.hiddenCategory.getSelectedItemPosition()).getSubCategories().get(position));
                        break;
                    case 5: //Users
                        DBManager.getDBManagerInstance().deleteUser(settings.getUsers().get(position));
                }

                updateNewExpenseSetting();

                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemRemoved(position);
            }

            @Override
            public void onSettingsAddListener(String name, int logo) {
                switch (rootPosition) {
                    case 0: // Payment Type
                        DBManager.getDBManagerInstance().insertPaymentType(new PaymentType(name, logo));
                        break;
                    case 1: //Expense Category
                        DBManager.getDBManagerInstance().insertExpenseCategory(new Category(name, logo));
                        break;
                    case 2: //Expense Sub-Category
                        DBManager.getDBManagerInstance().insertExpenseSubCategory(new SubCategory(name,  logo), 1);
                        break;
                    case 3: //Income Category
                        DBManager.getDBManagerInstance().insertIncomeCategory(new Category(name, logo));
                        break;
                    case 4: //Income Sub-Category
                        // how to take category id here?
                        DBManager.getDBManagerInstance().insertIncomeSubCategory(new SubCategory(name, logo), 1);
                        break;
                    case 5: //Users
                        DBManager.getDBManagerInstance().insertUser(new User(name));
                        break;
                }

                updateNewExpenseSetting();

                if(holder.hidden.getAdapter() != null)
                    holder.hidden.getAdapter().notifyItemInserted(holder.hidden.getAdapter().getItemCount() + 1);
            }
        };
        SettingsModifyAdapter settingsModifyAdapter;
        switch (rootPosition) {
            case 0: //Payment Type
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getPaymentMethod(), nameListener);
                break;
            case 1: //Expense Category
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getExpenseCategory(), nameListener);
                break;
            case 2: //Expense Sub-Category
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getExpenseSubCategory(0), nameListener);
                holder.hiddenCategory.setAdapter(new ComboBoxAdapter(currentContext, settings.getExpenseCategory(), 0));
                break;
            case 3: //Income Category
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getIncomeCategory(), nameListener);
                break;
            case 4: //Income Sub-Category
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getIncomeSubCategory(0), nameListener);
                holder.hiddenCategory.setAdapter(new ComboBoxAdapter(currentContext, settings.getIncomeCategory(), 0));
                break;
            case 5: //Users
                settingsModifyAdapter = new SettingsModifyAdapter(settings.getUsers(), nameListener);
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
                    holder.hidden.setAdapter(new SettingsModifyAdapter(settings.getExpenseSubCategory(position), nameListener));
                } else if(rootPosition == 4) {
                    holder.hidden.setAdapter(new SettingsModifyAdapter(settings.getIncomeSubCategory(position), nameListener));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        holder.hiddenButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            View diagView = View.inflate(holder.itemView.getContext(), R.layout.dialog_edittext, null);
            builder.setView(diagView);
            builder.setTitle("Add New Item");
            builder.setPositiveButton("Add", (dialogInterface, i) -> {
                if(rootPosition == 1) {
                    nameListener.onSettingsAddListener(((EditText) diagView.findViewById(R.id.dialog_edittext)).getText().toString(), R.color.grey);
                } else {
                    nameListener.onSettingsAddListener(((EditText) diagView.findViewById(R.id.dialog_edittext)).getText().toString(), R.drawable.ic_launcher_foreground);
                }
            });
            builder.setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}
