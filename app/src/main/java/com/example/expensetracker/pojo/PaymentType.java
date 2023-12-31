package com.example.expensetracker.pojo;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.JsonReader;
import android.util.JsonWriter;

import com.example.expensetracker.R;
import com.example.expensetracker.database.DBManager;
import com.example.expensetracker.database.DatabaseDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PaymentType extends SettingsParent implements JsonIO {
    int id;
    private String name;
    private int drawableId;


    private String tableName = "paymentMethod";

    public PaymentType() {
        name = "";
        drawableId = 0;
    }

    public PaymentType(int id, String name, int drawableId) {
        this.id = id;
        this.name = name;
        this.drawableId = drawableId;
    }

    public PaymentType(String name) {
        this.id = 0;
        this.name = name;
        this.drawableId = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public void readFromJson(JsonReader reader) throws IOException {
        reader.beginObject();
        if (Objects.equals(reader.nextName(), "name")) {
            this.name = reader.nextString();
        }
        if (Objects.equals(reader.nextName(), "drawId")) {
            this.drawableId = reader.nextInt();
        }
        reader.endObject();
    }
    @Override
    public void writeToJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("name").value(this.name);
        writer.name("drawId").value(this.drawableId);
        writer.endObject();
    }

    @Override
    public SettingsType getType() {
        return SettingsType.PAYMENT;
    }



    /*public static ArrayList<PaymentType> getPaymentType( )
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        Cursor cursor = dbManager.getData(DatabaseDetails.PAYMENTTYPE_NAME, null);

        ArrayList<PaymentType> data = new ArrayList<PaymentType>();
        do {
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int drawableId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("drawableId")));
            PaymentType obj = new PaymentType(id, name, drawableId);
            data.add(obj);
        } while(cursor.moveToNext());
        return data;
    }
     */
}
