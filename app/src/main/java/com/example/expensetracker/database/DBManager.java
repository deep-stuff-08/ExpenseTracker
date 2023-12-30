package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

    public final  static String DATABASE_NAME = "DB_ExpenseTracker.db";

    private static DBManager dbManager = null;

    private DBManager(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        onCreate();
     //   insertDefaultEntries();
    }

    public static DBManager getDBManagerInstance(Context context)
    {
        if(dbManager == null)
        {
            dbManager = new DBManager(context);
        }
        return dbManager;
    }

    public static DBManager getDBManagerInstance()
    {
        if(dbManager == null)
        {
            throw new RuntimeException();
        }
        return dbManager;
    }

    private SQLiteDatabase getDatabaseInstance()
    {
        return this.getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db)
    {

    }
    private void onCreate()
    {
        SQLiteDatabase db = getDatabaseInstance();

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

    // false - no record inserted
    // true - record inserted successfully
    private boolean insertDefaultEntries()
    {
        Cursor cursor = getData("category", null);
        if (null != cursor)
        {
            return false;
        }
        SQLiteDatabase db = this.getDatabaseInstance();
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
        String category_id = getData("category", "Name = 'Food'", "id");

        contentValues.clear();
        contentValues.put("Name", "Restaurant");
        contentValues.put("category_id", category_id);
        db.insert("subCategory", null, contentValues);

        contentValues.clear();
        contentValues.put("Name", "Grocery");
        contentValues.put("category_id", category_id);
        db.insert("subCategory", null, contentValues);

        // enter sub Categories of Housing
        category_id = getData("category", "Name = 'Housing'", "id");

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
    public boolean insert(ContentValues contentValues, String tableName)
    {
        getDatabaseInstance().insert(tableName, null, contentValues);
        return true;
    }

    public Cursor delete(String tableName, String condition) {
        String query = "delete from "+ tableName;
        query =  (null == condition) ? "" : " where " + condition;
        Cursor cursor =  getDatabaseInstance().rawQuery( query,
                null );
        return cursor;
    }

    public Cursor getData(String tableName, String condition) {
        String query = "select * from "+ tableName;
        query =  (null == condition) ? "" : " where " + condition;
        Cursor cursor =  getDatabaseInstance().rawQuery( query,
                null );
        if(null != cursor)
            cursor.moveToFirst();
        return cursor;
    }

    private String getData(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public String getData(String tableName, String condition, String columnName) {
        Cursor cursor = getData(tableName, condition);
        return getData(cursor, columnName);
    }

}
