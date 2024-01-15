package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.expensetracker.ExpenseSettings;
import com.example.expensetracker.R;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;

import java.util.ArrayList;

public class DBManager{
    private static DBManager dbManager = null;

    private SQLiteDatabase sqLiteDatabase ;

    private DBManager()
    {

    }

    private static boolean isDatabaseCreated(Context context)
    {
        try {
           SQLiteDatabase.openDatabase(context.getDatabasePath(DatabaseDetails.DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY).close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void createDBManagerInstance(Context context)
    {
        boolean databaseCreated = isDatabaseCreated(context);
        dbManager = new DBManager();
        if(!databaseCreated)
        {
            dbManager.sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(DatabaseDetails.DATABASE_NAME).getPath(), null);
            dbManager.onCreateSetup();
            dbManager.insertExpenseSettings(ExpenseSettings.createWithDefaultParameters(context));
        } else {
            dbManager.sqLiteDatabase = SQLiteDatabase.openDatabase(context.getDatabasePath(DatabaseDetails.DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    public static DBManager getDBManagerInstance()
    {
        if(null == dbManager)
        {
            throw new RuntimeException();
        }
        return dbManager;
    }

//    public boolean areTablesCreated()
//    {
//        Cursor cursor = getData("*", DatabaseDetails.CATEGORY_NAME, null);
//        if (null == cursor)
//        {
//            return false;
//        }
//
//        cursor = getData("*",DatabaseDetails.SUBCATEGORY_NAME, null);
//        if (null == cursor)
//        {
//            return false;
//        }
//
//        cursor = getData("*",DatabaseDetails.PAYMENTTYPE_NAME, null);
//        if (null == cursor)
//        {
//            return false;
//        }
//
//        cursor = getData("*",DatabaseDetails.EXPENSEENTRIES_NAME, null);
//        return null != cursor;
//    }

    public void onCreateSetup()
    {
        Log.d("DATABASE_LOG", "onCreate: called");

        // create table - category
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.CATEGORY_NAME +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL, color_id INTEGER)"
        );

        // create table - subCategory
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.SUBCATEGORY_NAME +
                        "(" +
                            "id INTEGER PRIMARY KEY, name text NOT NULL, category_id INTEGER," +
                            "FOREIGN KEY(category_id)" +
                            "REFERENCES category(id) " +
                        ")"
        );

        // create table - paymentMethod
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.PAYMENTTYPE_NAME +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL,drawable_id INTEGER DEFAULT 0)"
        );

        // create table - expenseTracker
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.EXPENSEENTRIES_NAME +
                        "(" +
                            "id INTEGER PRIMARY KEY, name text NOT NULL," +
                            "value REAL NOT NULL, category_id INTEGER NOT NULL," +
                            "subCategory_id INTEGER NOT NULL," +
                            "paymentMethod_id INTEGER, dateTime TEXT, isSharedExpense INTEGER" +
                        ")"
        );

        Log.d("DATABASE_LOG", "onCreate: exit");
    }

    // false - no record inserted
    // true - record inserted successfully
    public void insertExpenseSettings(ExpenseSettings expenseSettings)
    {
        for(PaymentType l : expenseSettings.getPaymentMethod()) {
            insertPaymentType(l);
        }

        for(Category l : expenseSettings.getCategory()) {
            // update subcategory
            insertCategory(l);
        }
    }

    public void insertCategory(Category cat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", cat.getName());
        contentValues.put("color_id", cat.getColorId());
        boolean ret = this.insert(contentValues, DatabaseDetails.CATEGORY_NAME);
        if(!ret) {
            throw new RuntimeException("Failed to insert data into database");
        }

        Cursor cursor = getData("id", DatabaseDetails.CATEGORY_NAME, "name = '" + cat.getName() + "' ");
        assert cursor != null;
        int catID = Integer.parseInt(cursor.getString(0));
        for(SubCategory sCat: cat.getSubCategories())
        {
            sCat.setCategoryId(catID);
            insertSubCategory(sCat);
        }
    }

    public void insertSubCategory(SubCategory scat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", scat.getName());
        contentValues.put("category_id", scat.getCategoryId());
        boolean ret = this.insert(contentValues, DatabaseDetails.SUBCATEGORY_NAME);
        if(!ret)
        {
            throw new RuntimeException("Failed to insert data into database");
        }
    }

    public void insertPaymentType(PaymentType p)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("drawable_id", p.getDrawableId());
        this.insert(contentValues, DatabaseDetails.PAYMENTTYPE_NAME);
    }

    public void updatePaymentType(PaymentType p)
    {
        String condition = "id = " + p.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("drawable_id", p.getDrawableId());
        this.update(DatabaseDetails.PAYMENTTYPE_NAME, contentValues, condition);
    }

    public void updateCategory(Category c)
    {
        String condition = "id = '" + c.getId() + "'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", c.getName());
        contentValues.put("color_id", c.getColorId());
        this.update(DatabaseDetails.CATEGORY_NAME, contentValues, condition);
    }

    public void updateSubCategory(SubCategory s)
    {
        String condition = "id = " + s.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", s.getName());
        contentValues.put("category_id", s.getCategoryId());
        this.update(DatabaseDetails.SUBCATEGORY_NAME, contentValues ,condition);
    }

    public void deletePaymentType(PaymentType p)
    {
        String condition = "id = '" + p.getId() +"'";
        this.delete(DatabaseDetails.PAYMENTTYPE_NAME, condition);
    }

    public void deleteCategory(Category c)
    {
        int categoryId = c.getId();
        for(SubCategory sCat: c.getSubCategories())
        {
            sCat.setCategoryId(categoryId);
            deleteSubCategory(sCat);
        }
        String condition = "id = '" + c.getId() +"'";
        this.delete(DatabaseDetails.CATEGORY_NAME, condition);
    }

    public void deleteSubCategory(SubCategory s)
    {
        String condition = "id = '" + s.getId() +"'";
        this.delete(DatabaseDetails.SUBCATEGORY_NAME, condition);
    }

    public boolean insert(ContentValues contentValues, String tableName)
    {
        sqLiteDatabase.insert(tableName, null, contentValues);
        return true;
    }

    public void delete(String tableName, String condition) {
        sqLiteDatabase.delete(tableName, condition, null);
        Log.d("DATABASE_LOG", "delete: exiting" );
    }

    public int update(String tableName, ContentValues cv, String condition) {
        int ret =  sqLiteDatabase.update(tableName, cv, condition, null);
        Log.d("DATABASE_LOG", "update: exiting");
        return ret;
    }

    public ArrayList<Category> getCategoryData() {
        ArrayList<Category> data = new ArrayList<>();
        String query = "select id, name, color_id from "+ DatabaseDetails.CATEGORY_NAME;
        try(Cursor cursor =  sqLiteDatabase.rawQuery( query,null )) {
            if(null == cursor)
            {
                throw new RuntimeException("cursor is null");
            }

            ArrayList<SubCategory> subCategories = this.getSubCategoryData();
            cursor.moveToFirst();
            do {
                ArrayList<SubCategory> subCategoriesByCategory = new ArrayList<>();
                int id = cursor.getInt(0);
                String name = cursor.getString(1);

                for (SubCategory subCat: subCategories) {
                    if(subCat.getCategoryId() == id)
                    {
                        subCategoriesByCategory.add(new SubCategory(subCat.getId(), subCat.getName(), subCat.getCategoryId(), subCat.getDrawableId()));
                    }
                }
                data.add(new Category(id, name, R.color.categoryYellow,subCategoriesByCategory));
            } while(cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public ArrayList<SubCategory> getSubCategoryData() {
        ArrayList<SubCategory>  data = new ArrayList<>();
        String query = "select id, name, category_id from "+ DatabaseDetails.SUBCATEGORY_NAME;
        try(Cursor cursor =  sqLiteDatabase.rawQuery( query,
                null )) {
            if (null != cursor)
                cursor.moveToFirst();

            do {
                assert cursor != null;
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String categoryId = cursor.getString(2);
                SubCategory obj = new SubCategory(Integer.parseInt(id), name, Integer.parseInt(categoryId), 0);
                data.add(obj);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<PaymentType> getPaymentData() {
        ArrayList<PaymentType> data = new ArrayList<>();
        String query = "select id, name from "+ DatabaseDetails.PAYMENTTYPE_NAME;
        try(Cursor cursor =  sqLiteDatabase.rawQuery(query,
                null)) {
            if (null != cursor)
                cursor.moveToFirst();

            do {
                assert cursor != null;
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                PaymentType obj = new PaymentType(Integer.parseInt(id), name, 0);
                data.add(obj);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public Cursor getData(String columnName, String tableName, String condition) {
        try {
            columnName =  (null == columnName) ? "*" :columnName;
            String query = "select " + columnName +" from " + tableName;
            query += (null == condition) ? "" : " where " + condition;
            Log.d("DATABASE_LOG", "getData: query = " + query);
            Cursor cursor = sqLiteDatabase.rawQuery(query,
                    null);
            if (null != cursor)
                cursor.moveToFirst();

            return cursor;
        }
        catch(Exception e) {
            return null;
        }
    }
}
