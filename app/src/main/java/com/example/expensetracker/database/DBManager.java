package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    public final  static String DATABASE_NAME = "DB_ExpenseTracker.db";

    public DBManager(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        onCreate();
        insertDefaultEntries();
    }

    public void onCreate(SQLiteDatabase db)
    {

    }
    public void onCreate()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // create table - category
        db.execSQL(
                "create table IF NOT EXISTS category " +
                        "(id INTEGER PRIMARY KEY, Name text NOT NULL)"
        );

        // create table - subCategory
        db.execSQL(
                "create table IF NOT EXISTS subCategory " +
                        "(" +
                            "id INTEGER PRIMARY KEY, Name text NOT NULL, category_id INTEGER," +
                            "FOREIGN KEY(category_id)" +
                            "REFERENCES category(id) " +
                        ")"
        );

        // create table - paymentMethod
        db.execSQL(
                "create table IF NOT EXISTS paymentMethod " +
                        "(id INTEGER PRIMARY KEY, Name text NOT NULL)"
        );

        // create table - expenseTracker
        db.execSQL(
                "create table IF NOT EXISTS expenseEntries" +
                        "(" +
                            "id INTEGER PRIMARY KEY, Name text NOT NULL," +
                            "value REAL NOT NULL, category_id INTEGER NOT NULL," +
                            "subCategory_id INTEGER NOT NULL," +
                            "paymentMethod_id INTEGER, dateTime TEXT, isSharedExpense INTEGER" +
                        ")"
        );

        Log.d("DATABASE_LOG", "onCreate: called");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public boolean insertDefaultEntries()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        // insert default values in "category" table
        contentValues.clear();
        contentValues.put("Name", "Food");
        db.insert("category", null, contentValues);
        contentValues.put("Name", "Housing");
        db.insert("category", null, contentValues);
        contentValues.put("Name", "Travel");
        db.insert("category", null, contentValues);
        contentValues.put("Name", "Entertainment");
        db.insert("category", null, contentValues);


        // insert default values in "subCategory" table
        // category_id

        // enter sub Categories of Food
        Cursor cursor = getData("category", "Name = 'Food'");
        String category_id = null;
        if (null != cursor)
        {
            cursor.moveToFirst();
            category_id = cursor.getString(cursor.getColumnIndex("id"));
        }
        contentValues.clear();
        contentValues.put("Name", "Restaurant");
        contentValues.put("category_id", category_id);
        db.insert("subCategory", null, contentValues);

        contentValues.clear();
        contentValues.put("Name", "Grocery");
        contentValues.put("category_id", category_id);
        db.insert("subCategory", null, contentValues);

        // enter sub Categories of Housing
        cursor = getData("category", "Name = 'Housing'");
        if (null != cursor)
        {
            cursor.moveToFirst();
            category_id = cursor.getString(cursor.getColumnIndex("id"));
        }
        contentValues.clear();
        contentValues.put("Name", "Rent");
        contentValues.put("category_id", category_id);
        db.insert("subCategory", null, contentValues);

        contentValues.clear();
        contentValues.put("Name", "Electricity");
        contentValues.put("category_id", category_id);
        db.insert("subCategory", null, contentValues);

        // insert default values in "paymentMethod" table
        contentValues.clear();
        contentValues.put("Name", "UPI");
        db.insert("paymentMethod", null, contentValues);
        contentValues.put("Name", "Cash");
        db.insert("paymentMethod", null, contentValues);
        contentValues.put("Name", "Credit Card");
        db.insert("paymentMethod", null, contentValues);

        return true;
    }
    public boolean insert()
    {
        return true;
    }

    public Cursor getData(String tableName, String condition) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+ tableName +" where " + condition, null );
        return res;
    }
}
