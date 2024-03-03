package com.example.expensetracker;

import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;

public class Settings {
    private final ArrayList<PaymentType> paymentMethod;
    private final ArrayList<Category> expenseCategory;
    private final ArrayList<Category> incomeCategory;
    private final ArrayList<User> users;

    private Settings() {
        paymentMethod = new ArrayList<>();
        expenseCategory = new ArrayList<>();
        incomeCategory = new ArrayList<>();
        users = new ArrayList<>();
    }

    private Settings(ArrayList<PaymentType> paymentMethod, ArrayList<Category> expenseCategory, ArrayList<Category> incomeCategory, ArrayList<User> users) {
        this.paymentMethod = paymentMethod;
        this.expenseCategory = expenseCategory;
        this.incomeCategory = incomeCategory;
        this.users = users;
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
    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<SubCategory> getExpenseSubCategory(int index) {
        return expenseCategory.get(index).getSubCategories();
    }

    public ArrayList<SubCategory> getIncomeSubCategory(int index) {
        return incomeCategory.get(index).getSubCategories();
    }

    public static Settings createWithDefaultParameters() {
        Settings s = new Settings();
        s.initToDefault();
        return s;
    }

    public static Settings createWithParametersFromDatabase()
    {
        DBManager db = DBManager.getDBManagerInstance();
        return new Settings(db.getPaymentData(), db.getExpenseCategoryData(), db.getIncomeCategoryData(), db.getUserData());
    }

    public void updateExpenseSettings(Settings newSettings)
    {
        this.expenseCategory.clear();
        this.expenseCategory.addAll(newSettings.getExpenseCategory());

        this.incomeCategory.clear();
        this.incomeCategory.addAll(newSettings.getIncomeCategory());

        this.paymentMethod.clear();
        this.paymentMethod.addAll(newSettings.getPaymentMethod());

        this.users.clear();
        this.users.addAll(newSettings.users);
    }

    public void initToDefault() {
        //Payment Method
        this.paymentMethod.clear();
        this.paymentMethod.add(new PaymentType("UPI", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new PaymentType("Cash", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new PaymentType("Credit Card", R.drawable.ic_launcher_foreground));

        //ExpenseCategory
        this.expenseCategory.clear();
        this.expenseCategory.add(new Category("Food", R.color.categoryOrange));
        this.expenseCategory.get(0).getSubCategories().add(new SubCategory("Restaurant", R.drawable.ic_launcher_foreground));
        this.expenseCategory.get(0).getSubCategories().add(new SubCategory("Grocery", R.drawable.ic_launcher_foreground));

        this.expenseCategory.add(new Category("Housing", R.color.categoryYellow));
        this.expenseCategory.get(1).getSubCategories().add(new SubCategory("Rent", R.drawable.ic_launcher_foreground));
        this.expenseCategory.get(1).getSubCategories().add(new SubCategory("Electricity", R.drawable.ic_launcher_foreground));

        this.expenseCategory.add(new Category("Travel", R.color.categoryBlue));
        this.expenseCategory.get(2).getSubCategories().add(new SubCategory("Public", R.drawable.ic_launcher_foreground));
        this.expenseCategory.get(2).getSubCategories().add(new SubCategory("Long Distance", R.drawable.ic_launcher_foreground));

        this.expenseCategory.add(new Category("Entertainment", R.color.categoryGreen));
        this.expenseCategory.get(3).getSubCategories().add(new SubCategory("Entry Fee", R.drawable.ic_launcher_foreground));
        this.expenseCategory.get(3).getSubCategories().add(new SubCategory("Trip", R.drawable.ic_launcher_foreground));

        //Income Category
        this.incomeCategory.clear();
        this.incomeCategory.add(new Category("Sony", R.color.categoryYellow));
        this.incomeCategory.get(0).getSubCategories().add(new SubCategory("Salary", R.drawable.ic_launcher_foreground));
        this.incomeCategory.get(0).getSubCategories().add(new SubCategory("Bonus", R.drawable.ic_launcher_foreground));

        //User
        this.users.clear();
        this.users.add(new User("Deep"));
        this.users.add(new User("Hrituja"));
    }
}
