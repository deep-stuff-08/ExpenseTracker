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
    public static class LogoNameCombo {
        private String name;
        private int logo;

        private static LogoNameCombo readFromJson(JsonReader reader) throws IOException {
            String name = "";
            int logo = 0;
            reader.beginObject();
            if (Objects.equals(reader.nextName(), "name")) {
                name = reader.nextString();
            }
            if (Objects.equals(reader.nextName(), "logo")) {
                logo = reader.nextInt();
            }
            reader.endObject();
            return new LogoNameCombo(name, logo);
        }

        private void writeToJson(JsonWriter writer) throws IOException {
            writer.beginObject();
            writer.name("name").value(this.name);
            writer.name("logo").value(this.logo);
            writer.endObject();
        }

        public LogoNameCombo(String name, int logo) {
            this.name = name;
            this.logo = logo;
        }

        public String getName() {
            return name;
        }

        public int getLogo() {
            return logo;
        }
    }

    private ExpenseSettings(Context context) {
        activityContext = context;
        paymentMethod = new ArrayList<>();
    }

    public void addPaymentMethod(LogoNameCombo payment) {
        paymentMethod.add(payment);
    }

    public void deletePaymentMethod(int position) {
        LogoNameCombo old = paymentMethod.remove(position);
        if(safeWriteJson()) {
            paymentMethod.add(position, old);
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
        if(!safeWriteJson()) {
            paymentMethod.get(position).logo = old;
        }
    }

    public ArrayList<LogoNameCombo> getPaymentMethod() {
        return paymentMethod;
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
            if (Objects.equals(reader.nextName(), "PaymentMethod")) {
                s.readPaymentMethod(reader);
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
    }

    public void writeSettingsToJson(File f) throws IOException {
        FileOutputStream fos = new FileOutputStream(f, false);
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
        writer.setIndent("\t");
        writer.beginObject();
        writePaymentMethod(writer);
        writer.endObject();
        writer.close();
    }

    //Specific Member Reader and Writers
    private void readPaymentMethod(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            paymentMethod.add(LogoNameCombo.readFromJson(reader));
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

    public interface SettingsUpdateListener {
        void onSettingsNameUpdateListener(int position, String newName);
        void onSettingsLogoUpdateListener(int position, int newLogo);
        void onSettingsNameLogoComboDeleteListener(int position);
        void onSettingsNameLogoComboAddListener(String name, int logo);
    }
}
