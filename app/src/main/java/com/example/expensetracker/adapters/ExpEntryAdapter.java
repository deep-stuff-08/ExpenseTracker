package com.example.expensetracker.adapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

    public ExpEntryAdapter(MainActivity activity, ArrayList<Entry> entries) {
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
            holder.value.setText(String.format(Locale.getDefault(), "%s%d", sign, entries.get(position).getValue()));
            holder.image.setImageResource(Objects.requireNonNull(expenseSubCategoryHashMap.get(entries.get(position).getSubCategoryId())).getDrawableId());
            holder.image.setBackgroundColor(Objects.requireNonNull(expenseCategoryHashMap.get(entries.get(position).getCategoryId())).getColorId());
        }
    }
    @Override
    public int getItemCount() {
        return entries.size() + 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView name;
        public final TextView payment;
        public final TextView value;
        public final ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.expense_name);
            payment = itemView.findViewById(R.id.payment_type);
            value = itemView.findViewById(R.id.expense_payment_value);
            image = itemView.findViewById(R.id.imageview);
        }
    }
}
