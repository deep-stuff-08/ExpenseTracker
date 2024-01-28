package com.example.expensetracker;

import android.content.Context;

import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;
import java.util.Arrays;

public class ExpenseSettings {
    private final ArrayList<PaymentType> paymentMethod;
    private final ArrayList<Category> expenseCategory;

    private final ArrayList<Category> incomeCategory;

    private ExpenseSettings() {
        paymentMethod = new ArrayList<>();
        expenseCategory = new ArrayList<>();
        incomeCategory = new ArrayList<>();
    }

    private ExpenseSettings(ArrayList<PaymentType> paymentMethod, ArrayList<Category> expenseCategory, ArrayList<Category> incomeCategory) {
        this.paymentMethod = paymentMethod;
        this.expenseCategory = expenseCategory;
        this.incomeCategory = incomeCategory;
    }

    public ArrayList<PaymentType> getPaymentMethod() {
        return paymentMethod;
    }
    public ArrayList<Category> getExpenseCategory() {
        return expenseCategory;
    }

    public ArrayList<Category> getIncomeCategory() {
       return incomeCategory;
    }

    public ArrayList<SubCategory> getExpenseSubCategory(int index) {
        return expenseCategory.get(index).getSubCategories();
    }

    public ArrayList<SubCategory> getIncomeSubCategory(int index) {
        return incomeCategory.get(index).getSubCategories();
    }

	public ArrayList<User> getUsers() {
        return new ArrayList<>(Arrays.asList(
                new User("Hrituja"),
                new User("Darshan")
        ));
    }

    public static ExpenseSettings createWithDefaultParameters() {
        ExpenseSettings s = new ExpenseSettings();
        s.initToDefault();
        return s;
    }

    public static ExpenseSettings createWithParametersFromDatabase()
    {
        DBManager db = DBManager.getDBManagerInstance();
        return new ExpenseSettings(db.getPaymentData(), db.getExpenseCategoryData(), db.getIncomeCategoryData());
    }

    public void updateExpenseSettings(ExpenseSettings newExpenseSettings)
    {
        this.expenseCategory.clear();
        this.expenseCategory.addAll(newExpenseSettings.getExpenseCategory());

        this.incomeCategory.clear();
        this.incomeCategory.addAll(newExpenseSettings.getIncomeCategory());

        this.paymentMethod.clear();
        this.paymentMethod.addAll(newExpenseSettings.getPaymentMethod());
    }

    public void initToDefault() {
        //Payment Method
        this.paymentMethod.add(new PaymentType(0, "UPI", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new PaymentType(0, "Cash", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new PaymentType(0, "Credit Card", R.drawable.ic_launcher_foreground));

        this.expenseCategory.add(new Category(0, "Food", R.color.categoryOrange, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Restaurant", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Grocery", 0, R.drawable.ic_launcher_foreground)
        ))));
        this.expenseCategory.add(new Category(0,"Housing", R.color.categoryYellow, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Rent", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Electricity", 0, R.drawable.ic_launcher_foreground)
        ))));
        this.expenseCategory.add(new Category(0,"Travel", R.color.categoryBlue, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Public", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Long Distance", 0, R.drawable.ic_launcher_foreground)
        ))));
        this.expenseCategory.add(new Category(0,"Entertainment", R.color.categoryGreen, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Entry Fee", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Trip", 0, R.drawable.ic_launcher_foreground)
        ))));
    }
}
