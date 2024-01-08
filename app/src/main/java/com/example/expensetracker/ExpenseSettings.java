package com.example.expensetracker;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.Toast;

import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.IncomeSource;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.Type;
import com.example.expensetracker.pojo.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ExpenseSettings {
    private final Context activityContext;
    private final ArrayList<PaymentType> paymentMethod;
    private final ArrayList<Category> category;

    private ExpenseSettings(Context context) {
        activityContext = context;
        paymentMethod = new ArrayList<>();
        category = new ArrayList<>();
    }

    private ExpenseSettings(Context context, ArrayList<PaymentType> paymentMethod, ArrayList<Category> category) {
        activityContext = context;
        this.paymentMethod = paymentMethod;
        this.category = category;
    }

    private boolean safeWriteJson() {
        try {
            File f = new File(activityContext.getFilesDir(), activityContext.getString(R.string.settings_filename));
            writeSettingsToJson(f);
            return false;
        } catch(IOException e) {
            Toast.makeText(activityContext, "Unable to update settings, any changes you make will be temporary.", Toast.LENGTH_LONG).show();
            return true;
        }
    }
    public ArrayList<PaymentType> getPaymentMethod() {
        return paymentMethod;
    }
    public ArrayList<Category> getCategory() {
        return category;
    }
    public ArrayList<SubCategory> getSubCategory(int index) {
        return category.get(index).getSubCategories();
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(Arrays.asList(
                new User("Hrituja"),
                new User("Darshan")
        ));
    }

    public ArrayList<IncomeSource> getIncomeSources() {
        return new ArrayList<>(Arrays.asList(
                new IncomeSource("Salary"),
                new IncomeSource("Investment")
        ));
    }

    public ArrayList<Type> getTypes() {
        return new ArrayList<>(Arrays.asList(
                new Type("Income"),
                new Type("Settlement")
        ));
    }

    public static ExpenseSettings createWithDefaultParameters(Context context) {
        ExpenseSettings s = new ExpenseSettings(context);
        s.initToDefault();
        return s;
    }

    private static void initializeDatabase(Context context)
    {
        DBManager dbManager = DBManager.createDBManagerInstance(context);

        ExpenseSettings expenseSettingsWithDefaultValues = new ExpenseSettings(context, new ArrayList<PaymentType>(), new ArrayList<Category>());
        if(!dbManager.areTablesCreated())
        {
            dbManager.onCreateSetup();
            expenseSettingsWithDefaultValues.initToDefault();
            dbManager.insertExpenseSettings(expenseSettingsWithDefaultValues);
        }
    }

    public static ExpenseSettings readDataFromDatabase(Context context)
    {
        initializeDatabase(context);

        // read back data
        return new ExpenseSettings(context, DBManager.getPaymentData(), DBManager.getCategoryData());
    }

    public static void updateExpenseSettings(ExpenseSettings setting, ExpenseSettings newExpenseSettings)
    {
        setting.category.clear();
        setting.category.addAll(newExpenseSettings.getCategory());

        setting.paymentMethod.clear();
        setting.paymentMethod.addAll(newExpenseSettings.getPaymentMethod());
    }

    public static ExpenseSettings createWithParametersFromFile(Context context, File f) throws IOException {
        ExpenseSettings s = new ExpenseSettings(context);
        FileInputStream fis = new FileInputStream(f);
        JsonReader reader = new JsonReader(new InputStreamReader(fis));
        reader.beginObject();
        while (reader.hasNext()) {
            String nextName = reader.nextName();
            if (Objects.equals(nextName, "PaymentMethod")) {
                s.readPaymentMethod(reader);
            } else if(Objects.equals(nextName, "Category")) {
                s. readCategory(reader);
            }
        }
        reader.endObject();
        return s;
    }

    public void initToDefault() {
        //Payment Method
        this.paymentMethod.add(new PaymentType(0, "UPI", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new PaymentType(0, "Cash", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new PaymentType(0, "Credit Card", R.drawable.ic_launcher_foreground));

        this.category.add(new Category(0, "Food", R.color.categoryOrange, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Restaurant", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Grocery", 0, R.drawable.ic_launcher_foreground)
        ))));
        this.category.add(new Category(0,"Housing", R.color.categoryYellow, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Rent", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Electricity", 0, R.drawable.ic_launcher_foreground)
        ))));
        this.category.add(new Category(0,"Travel", R.color.categoryBlue, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Public", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Long Distance", 0, R.drawable.ic_launcher_foreground)
        ))));
        this.category.add(new Category(0,"Entertainment", R.color.categoryGreen, new ArrayList<>(Arrays.asList(
                new SubCategory(0,"Entry Fee", 0, R.drawable.ic_launcher_foreground),
                new SubCategory(0,"Trip", 0, R.drawable.ic_launcher_foreground)
        ))));
    }

    public void writeSettingsToJson(File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f, false);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
        writer.setIndent("\t");
        writer.beginObject();
        writePaymentMethod(writer);
        writeCategory(writer);
        writer.endObject();
        writer.close();
    }

    //Specific Member Reader and Writers
    private void readPaymentMethod(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            PaymentType paymentType = new PaymentType();
            paymentType.readFromJson(reader);
            paymentMethod.add(paymentType);
        }
        reader.endArray();
    }

    private void writePaymentMethod(JsonWriter writer) throws IOException {
        writer.name("PaymentMethod");
        writer.beginArray();
        for(PaymentType l : paymentMethod) {
            l.writeToJson(writer);
        }
        writer.endArray();
    }

    private void readCategory(JsonReader reader) throws IOException {
        reader.beginArray();
        while(reader.hasNext()) {
            Category category1 = new Category();
            category1.readFromJson(reader);
            category.add(category1);
        }
        reader.endArray();
    }

    private void writeCategory(JsonWriter writer) throws IOException {
        writer.name("Category");
        writer.beginArray();
        for(Category l : category) {
            l.writeToJson(writer);
        }
        writer.endArray();
    }

    public void addPaymentMethod(PaymentType payment) {
        paymentMethod.add(payment);
        if(safeWriteJson()) {
            paymentMethod.remove(payment);
        }
    }

    public void addCategory(Category cat) {
        category.add(cat);
        if(safeWriteJson()) {
            paymentMethod.remove(cat);
        }
    }

    public void addSubCategory(int categoryId, SubCategory subCat) {
        category.get(categoryId).getSubCategories().add(subCat);
        if(safeWriteJson()) {
            category.get(categoryId).getSubCategories().remove(subCat);
        }
    }

    public void deletePaymentMethod(int position) {
        PaymentType old = paymentMethod.remove(position);
        if(safeWriteJson()) {
            paymentMethod.add(position, old);
        }
    }

    public void deleteCategory(int position) {
        Category old = category.remove(position);
        if(safeWriteJson()) {
            category.add(position, old);
        }
    }

    public void deleteSubCategory(int categoryId, int position) {
        SubCategory old = category.get(categoryId).getSubCategories().remove(position);
        if(safeWriteJson()) {
            category.get(categoryId).getSubCategories().add(position, old);
        }
    }

    public void updatePaymentMethodName(int position, String name) {
        String old = paymentMethod.get(position).getName();
        paymentMethod.get(position).setName(name);
        if(safeWriteJson()) {
            paymentMethod.get(position).setName(old);
        }
    }
    public void updatePaymentMethodDrawId(int position, int logo) {
        int old = paymentMethod.get(position).getDrawableId();
        paymentMethod.get(position).setDrawableId(logo);
        if(safeWriteJson()) {
            paymentMethod.get(position).setDrawableId(old);
        }
    }

    public void updateCategoryName(int position, String name) {
        String old = category.get(position).getName();
        category.get(position).setName(name);
        if(safeWriteJson()) {
            category.get(position).setName(old);
        }
    }
    public void updateCategoryColorId(int position, int logo) {
        int old = category.get(position).getColorId();
        category.get(position).setColorId(logo);
        if(safeWriteJson()) {
            category.get(position).setColorId(old);
        }
    }

    public void updateSubCategoryName(int categoryId, int position, String name) {
        String old = category.get(categoryId).getSubCategories().get(position).getName();
        category.get(categoryId).getSubCategories().get(position).setName(name);
        if(safeWriteJson()) {
            category.get(categoryId).getSubCategories().get(position).setName(old);
        }
    }

    public void updateSubCategoryDrawId(int categoryId, int position, int logo) {
        int old = category.get(categoryId).getSubCategories().get(position).getDrawableId();
        category.get(categoryId).getSubCategories().get(position).setDrawableId(logo);
        if(safeWriteJson()) {
            category.get(categoryId).getSubCategories().get(position).setDrawableId(old);
        }
    }
}
