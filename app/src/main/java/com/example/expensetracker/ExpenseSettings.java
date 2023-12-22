package com.example.expensetracker;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonWriter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;

public class ExpenseSettings {
    private final Context activityContext;
    private final ArrayList<LogoNameCombo> paymentMethod;
    private final ArrayList<LogoNameCombo> category;

    public interface JsonIO {
        void readFromJson(JsonReader reader) throws IOException;
        void writeToJson(JsonWriter writer) throws IOException;
    }

    public static class LogoNameCombo implements JsonIO {
        private String name;
        private int logo;

        public void readFromJson(JsonReader reader) throws IOException {
            reader.beginObject();
            if (Objects.equals(reader.nextName(), "name")) {
                this.name = reader.nextString();
            }
            if (Objects.equals(reader.nextName(), "logo")) {
                this.logo = reader.nextInt();
            }
            reader.endObject();
        }

        public void writeToJson(JsonWriter writer) throws IOException {
            writer.beginObject();
            writer.name("name").value(this.name);
            writer.name("logo").value(this.logo);
            writer.endObject();
        }

        public LogoNameCombo(String name, int logo) {
            this.name = name;
            this.logo = logo;
        }
        public LogoNameCombo() {
            this.name = "";
            this.logo = 0;
        }

        public String getName() {
            return name;
        }

        public int getLogo() {
            return logo;
        }
    }

    public static class Category {
        private LogoNameCombo name;
        private ArrayList<LogoNameCombo> subCategories;

        public Category(LogoNameCombo name, ArrayList<LogoNameCombo> subCategories) {
            this.name = name;
            this.subCategories = subCategories;
        }

        public LogoNameCombo getName() {
            return name;
        }

        public ArrayList<LogoNameCombo> getSubCategories() {
            return subCategories;
        }
    }

    private ExpenseSettings(Context context) {
        activityContext = context;
        paymentMethod = new ArrayList<>();
        category = new ArrayList<>();
    }

    public void addPaymentMethod(LogoNameCombo payment) {
        paymentMethod.add(payment);
        if(safeWriteJson()) {
            paymentMethod.remove(payment);
        }
    }

    public void addCategory(LogoNameCombo cat) {
        category.add(cat);
        if(safeWriteJson()) {
            category.remove(cat);
        }
    }

    public void deletePaymentMethod(int position) {
        LogoNameCombo old = paymentMethod.remove(position);
        if(safeWriteJson()) {
            paymentMethod.add(position, old);
        }
    }

    public void deleteCategory(int position) {
        LogoNameCombo old = category.remove(position);
        if(safeWriteJson()) {
            category.add(position, old);
        }
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

    public void updatePaymentMethodName(int position, String name) {
        String old = paymentMethod.get(position).name = name;
        if(safeWriteJson()) {
            paymentMethod.get(position).name = old;
        }
    }
    public void updatePaymentMethodLogo(int position, int logo) {
        int old = paymentMethod.get(position).logo = logo;
        if(safeWriteJson()) {
            paymentMethod.get(position).logo = old;
        }
    }

    public void updateCategoryName(int position, String name) {
        String old = category.get(position).name = name;
        if(safeWriteJson()) {
            category.get(position).name = old;
        }
    }
    public void updateCategoryLogo(int position, int logo) {
        int old = category.get(position).logo = logo;
        if(safeWriteJson()) {
            category.get(position).logo = old;
        }
    }
    public ArrayList<LogoNameCombo> getPaymentMethod() {
        return paymentMethod;
    }

    public ArrayList<LogoNameCombo> getCategory() {
        return category;
    }

    public static ExpenseSettings createWithDefaultParameters(Context context) {
        ExpenseSettings s = new ExpenseSettings(context);

        s.initToDefault();

        return s;
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
                s.readCategory(reader);
            }
        }
        reader.endObject();
        return s;
    }

    public void initToDefault() {
        //Payment Method
        this.paymentMethod.add(new LogoNameCombo("UPI", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new LogoNameCombo("Cash", R.drawable.ic_launcher_foreground));
        this.paymentMethod.add(new LogoNameCombo("Credit Card", R.drawable.ic_launcher_foreground));

        this.category.add(new LogoNameCombo("Food", R.color.categoryOrange));
        this.category.add(new LogoNameCombo("Housing", R.color.categoryYellow));
        this.category.add(new LogoNameCombo("Travel", R.color.categoryBlue));
        this.category.add(new LogoNameCombo("Entertainment", R.color.categoryGreen));
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
            LogoNameCombo logoNameCombo = new LogoNameCombo();
            logoNameCombo.readFromJson(reader);
            paymentMethod.add(logoNameCombo);
        }
        reader.endArray();
    }

    private void writePaymentMethod(JsonWriter writer) throws IOException {
        writer.name("PaymentMethod");
        writer.beginArray();
        for(LogoNameCombo l : paymentMethod) {
            l.writeToJson(writer);
        }
        writer.endArray();
    }

    private void readCategory(JsonReader reader) throws IOException {
        reader.beginArray();
        while(reader.hasNext()) {
            LogoNameCombo logoNameCombo = new LogoNameCombo();
            logoNameCombo.readFromJson(reader);
            category.add(logoNameCombo);
        }
        reader.endArray();
    }

    private void writeCategory(JsonWriter writer) throws IOException {
        writer.name("Category");
        writer.beginArray();
        for(LogoNameCombo l : category) {
            l.writeToJson(writer);
        }
        writer.endArray();
    }

    public interface SettingsUpdateListener {
        void onSettingsNameUpdateListener(int position, String newName);
        void onSettingsLogoUpdateListener(int position, int newLogo);
        void onSettingsNameLogoComboDeleteListener(int position);
        void onSettingsNameLogoComboAddListener(String name, int logo);
    }
}
