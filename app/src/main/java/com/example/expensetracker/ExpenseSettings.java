package com.example.expensetracker;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;

public class ExpenseSettings {
    private final ArrayList<LogoNameCombo> paymentMethod;
    public static class LogoNameCombo {
        private final String name;
        private final int logo;

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

    private ExpenseSettings() {
        paymentMethod = new ArrayList<>();
    }

    public void addPaymentMethod(LogoNameCombo paymentMethod) {
        this.paymentMethod.add(paymentMethod);
    }

    public ArrayList<LogoNameCombo> getPaymentMethod() {
        return paymentMethod;
    }

    public static ExpenseSettings createWithDefaultParameters() {
        ExpenseSettings s = new ExpenseSettings();

        s.initToDefault();

        return s;
    }

    public static ExpenseSettings createWithParametersFromFile(File f) throws IOException {
        ExpenseSettings s = new ExpenseSettings();
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
}
