package com.example.expensetracker;

import android.util.JsonReader;
import android.util.JsonWriter;
import android.util.Log;

import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.database.DatabaseDetails;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Pattern;

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

    public void setPaymentMethod(ArrayList<PaymentType> paymentMethodArray) {
        paymentMethod.clear();
        paymentMethod.addAll(paymentMethodArray);
    }

    public void setExpenseCategory(ArrayList<Category> expenseCategoryArray) {
        expenseCategory.clear();
        expenseCategory.addAll(expenseCategoryArray);
    }

    public void setIncomeCategory(ArrayList<Category> expenseCategoryArray) {
        incomeCategory.clear();
        incomeCategory.addAll(expenseCategoryArray);
    }

    public void setUsers(ArrayList<User> userArray) {
        users.clear();
        users.addAll(userArray);
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

    public void exportToJson(FileOutputStream fos) throws IOException {
        JsonWriter js = new JsonWriter(new OutputStreamWriter(fos));
        js.setIndent("\t");

        js.beginObject();
        js.name(DatabaseDetails.PAYMENT_TYPE);
        js.beginArray();
        for(PaymentType pt : this.paymentMethod) {
            js.value(pt.getId() + "|" + pt.getName() + "|" + pt.getDrawableId());
        }
        js.endArray();

        js.name(DatabaseDetails.USERS);
        js.beginArray();
        for(User u : this.users) {
            js.value(u.getId() + "|" + u.getName() + "|" + u.getDrawableId());
        }
        js.endArray();

        js.name(DatabaseDetails.SUBCATEGORY_EXPENSE);
        js.beginArray();
        for(Category c : this.expenseCategory) {
            for(SubCategory sc : c.getSubCategories()) {
                js.value(sc.getId() + "|" + sc.getName() + "|" + sc.getDrawableId());
            }
        }
        js.endArray();

        js.name(DatabaseDetails.CATEGORY_EXPENSE);
        js.beginArray();
        for(Category c : this.expenseCategory) {
            StringBuilder subCat = new StringBuilder();
            for(SubCategory sc : c.getSubCategories()) {
                subCat.append(sc.getId());
                subCat.append(',');
            }
            subCat.deleteCharAt(subCat.length() - 1);
            js.value(c.getId() + "|" + c.getName() + "|" + c.getColorId() + "|" + subCat);
        }
        js.endArray();

        js.name(DatabaseDetails.SUBCATEGORY_INCOME);
        js.beginArray();
        for(Category c : this.incomeCategory) {
            for(SubCategory sc : c.getSubCategories()) {
                js.value(sc.getId() + "|" + sc.getName() + "|" + sc.getDrawableId());
            }
        }
        js.endArray();

        js.name(DatabaseDetails.CATEGORY_INCOME);
        js.beginArray();
        for(Category c : this.incomeCategory) {
            StringBuilder subCat = new StringBuilder();
            for(SubCategory sc : c.getSubCategories()) {
                subCat.append(sc.getId());
                subCat.append(',');
            }
            subCat.deleteCharAt(subCat.length() - 1);
            js.value(c.getId() + "|" + c.getName() + "|" + c.getColorId() + "|" + subCat);
        }
        js.endArray();

        js.endObject();

        js.close();
    }

    public void importFromJson(FileInputStream inputStream) throws IOException {
        JsonReader js = new JsonReader(new InputStreamReader(inputStream));
        js.beginObject();

        if(!Objects.equals(js.nextName(), DatabaseDetails.PAYMENT_TYPE)) throw new RuntimeException("Couldn't find key : " + DatabaseDetails.PAYMENT_TYPE);
        this.paymentMethod.clear();
        js.beginArray();
        while(js.hasNext()){
            String[] s = js.nextString().split(Pattern.quote("|"));
            this.paymentMethod.add(new PaymentType(Integer.parseInt(s[0]), s[1], Integer.parseInt(s[2])));
        }
        js.endArray();

        if(!Objects.equals(js.nextName(), DatabaseDetails.USERS)) throw new RuntimeException("Couldn't find key : " + DatabaseDetails.USERS);
        this.users.clear();
        js.beginArray();
        while(js.hasNext()){
            String[] s = js.nextString().split(Pattern.quote("|"));
            this.users.add(new User(Integer.parseInt(s[0]), s[1]));
        }
        js.endArray();

        if(!Objects.equals(js.nextName(), DatabaseDetails.SUBCATEGORY_EXPENSE)) throw new RuntimeException("Couldn't find key : " + DatabaseDetails.SUBCATEGORY_EXPENSE);
        HashMap<Long, SubCategory> expSub = new HashMap<>();
        js.beginArray();
        while(js.hasNext()){
            String[] s = js.nextString().split(Pattern.quote("|"));
            long key = Long.parseLong(s[0]);
            expSub.put(key, new SubCategory(key, s[1], 0, Integer.parseInt(s[2])));
        }
        js.endArray();

        if(!Objects.equals(js.nextName(), DatabaseDetails.CATEGORY_EXPENSE)) throw new RuntimeException("Couldn't find key : " + DatabaseDetails.CATEGORY_EXPENSE);
        this.expenseCategory.clear();
        js.beginArray();
        while(js.hasNext()){
            String[] s = js.nextString().split(Pattern.quote("|"));
            String[] sub = s[3].split(",");
            ArrayList<SubCategory> sc = new ArrayList<>();
            long id = Long.parseLong(s[0]);
            for(String v : sub) {
                Long l = Long.valueOf(v);
                SubCategory subCategory = expSub.get(l);
                if(subCategory == null) throw new RuntimeException("Couldn't find sub-category with id : " + v);
                subCategory.setCategoryId(id);
                sc.add(subCategory);
                expSub.remove(l);
            }
            this.expenseCategory.add(new Category(id, s[1], Integer.parseInt(s[2]), sc));
        }
        js.endArray();

        if(!Objects.equals(js.nextName(), DatabaseDetails.SUBCATEGORY_INCOME)) throw new RuntimeException("Couldn't find key : " + DatabaseDetails.SUBCATEGORY_INCOME);
        HashMap<Long, SubCategory> incSub = new HashMap<>();
        js.beginArray();
        while(js.hasNext()){
            String[] s = js.nextString().split(Pattern.quote("|"));
            long key = Long.parseLong(s[0]);
            incSub.put(key, new SubCategory(key, s[1], 0, Integer.parseInt(s[2])));
        }
        js.endArray();

        if(!Objects.equals(js.nextName(), DatabaseDetails.CATEGORY_INCOME)) throw new RuntimeException("Couldn't find key : " + DatabaseDetails.CATEGORY_INCOME);
        js.beginArray();
        while(js.hasNext()){
            String[] s = js.nextString().split(Pattern.quote("|"));
            String[] sub = s[3].split(",");
            ArrayList<SubCategory> sc = new ArrayList<>();
            long id = Long.parseLong(s[0]);
            for(String v : sub) {
                Long l = Long.valueOf(v);
                SubCategory subCategory = incSub.get(l);
                if(subCategory == null) throw new RuntimeException("Couldn't find sub-category with id : " + v);
                subCategory.setCategoryId(id);
                sc.add(subCategory);
                incSub.remove(l);
            }
            this.incomeCategory.add(new Category(id, s[1], Integer.parseInt(s[2]), sc));
        }
        js.endArray();

        js.endObject();
        js.close();
    }
}
