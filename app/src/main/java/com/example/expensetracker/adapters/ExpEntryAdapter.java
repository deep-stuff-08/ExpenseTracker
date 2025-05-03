package com.example.expensetracker.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.Entry;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ExpEntryAdapter extends RecyclerView.Adapter<ExpEntryAdapter.ViewHolder> {
    ArrayList<Entry> entries;
    MainActivity activity;
    HashMap<Long, PaymentType> paymentTypeHashMap;
    HashMap<Long, Category> expenseCategoryHashMap;
    HashMap<Long, SubCategory> expenseSubCategoryHashMap;
    HashMap<Long, Category> incomeCategoryHashMap;
    HashMap<Long, SubCategory> incomeSubCategoryHashMap;
    SelectionTracker<Long> selectionTracker;
    public ExpEntryAdapter(MainActivity activity, ArrayList<Entry> entries) {
        setHasStableIds(true);
        this.activity = activity;
        this.entries = entries;
        paymentTypeHashMap = new HashMap<>();
        activity.getSettings().getPaymentMethod().forEach(paymentType -> paymentTypeHashMap.put(paymentType.getId(), paymentType));
        expenseCategoryHashMap = new HashMap<>();
        activity.getSettings().getExpenseCategory().forEach(category -> expenseCategoryHashMap.put(category.getId(), category));
        expenseSubCategoryHashMap = new HashMap<>();
        for(Category c : activity.getSettings().getExpenseCategory()) {
            c.getSubCategories().forEach(subCategory -> expenseSubCategoryHashMap.put(subCategory.getId(), subCategory));
        }
        incomeCategoryHashMap = new HashMap<>();
        activity.getSettings().getIncomeCategory().forEach(category -> incomeCategoryHashMap.put(category.getId(), category));
        incomeSubCategoryHashMap = new HashMap<>();
        for(Category c : activity.getSettings().getIncomeCategory()) {
            c.getSubCategories().forEach(subCategory -> incomeSubCategoryHashMap.put(subCategory.getId(), subCategory));
        }
    }
    @Override
    public long getItemId(int position) {
        if(position < entries.size()) {
            return entries.get(position).getId();
        } else {
            return position;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_expenses, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position < entries.size()) {
            holder.name.setText(entries.get(position).getName());
            holder.payment.setText(Objects.requireNonNull(paymentTypeHashMap.get(entries.get(position).getPaymentId())).getName());
            String sign = "";
            if (entries.get(position).getValue() < 0) {
                holder.value.setTextColor(activity.getColor(R.color.lossRed));
            } else {
                holder.value.setTextColor(activity.getColor(R.color.profitGreen));
                sign = "+";
            }
            holder.value.setText(String.format(Locale.getDefault(), "%s%.2f", sign, entries.get(position).getValue()));
            holder.image.setImageResource(Objects.requireNonNull(expenseSubCategoryHashMap.get(entries.get(position).getSubCategoryId())).getDrawableId());
            holder.image.setBackgroundColor(Objects.requireNonNull(expenseCategoryHashMap.get(entries.get(position).getCategoryId())).getColorId());
            holder.id = entries.get(position).getId();
            boolean isSelected = selectionTracker.isSelected(entries.get(position).getId());
            holder.itemView.setActivated(isSelected);
            holder.checkBox.setChecked(isSelected);
        }
    }
    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView payment;
        public final TextView value;
        public final ImageView image;
        public final CheckBox checkBox;
        public Long id;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.expense_name);
            payment = itemView.findViewById(R.id.payment_type);
            value = itemView.findViewById(R.id.expense_payment_value);
            image = itemView.findViewById(R.id.imageview);
            checkBox = itemView.findViewById(R.id.expense_is_selected);
            checkBox.setClickable(false);
            id = 0L;
        }

        ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            return new ItemDetailsLookup.ItemDetails<Long>() {
                @Override
                public int getPosition() {
                    return getAdapterPosition();
                }

                @Nullable
                @Override
                public Long getSelectionKey() {
                    return id;
                }
            };
        }
    }
}
