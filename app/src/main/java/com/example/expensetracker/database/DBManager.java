package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.expensetracker.ExpenseSettings;
import com.example.expensetracker.R;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {


    private static DBManager dbManager = null;
    private ArrayList<Category> data;

    private DBManager(Context context)
    {
        super(context, DatabaseDetails.DATABASE_NAME, null, 1);
    }

    public static DBManager createDBManagerInstance(Context context)
    {
        dbManager = new DBManager(context);
        return dbManager;
    }

    public static DBManager getDBManagerInstance()
    {
        if(null == dbManager)
        {
            throw new RuntimeException();
        }
        return dbManager;
    }

    private SQLiteDatabase getDatabaseInstance()
    {
        return this.getWritableDatabase();
    }

    private boolean isDatabaseCreated()
    {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DatabaseDetails.DATABASE_NAME, null, SQLiteDatabase.OPEN_READONLY);
            db.close();
        }
        catch (Exception e) { }
        return db == null;
    }

    public boolean areTablesCreated()
    {
        Cursor cursor = getData(DatabaseDetails.CATEGORY_NAME, null);
        if (null == cursor)
        {
            return false;
        }

        cursor = getData(DatabaseDetails.SUBCATEGORY_NAME, null);
        if (null == cursor)
        {
            return false;
        }

        cursor = getData(DatabaseDetails.PAYMENTTYPE_NAME, null);
        if (null == cursor)
        {
            return false;
        }

        cursor = getData(DatabaseDetails.EXPENSEENTRIES_NAME, null);
        if (null == cursor)
        {
            return false;
        }

        return true;
    }
    
    public boolean isDatabaseSetup()
    {
        boolean ret = isDatabaseCreated();
        if(ret) return false;
        return areTablesCreated();
    }

    public void onCreate(SQLiteDatabase db)
    {

    }

    public void onCreateSetup()
    {
        Log.d("DATABASE_LOG", "onCreate: called");

        SQLiteDatabase db = getDatabaseInstance();

        // create table - category
        db.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.CATEGORY_NAME +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL, color_id INTEGER)"
        );

        // create table - subCategory
        db.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.SUBCATEGORY_NAME +
                        "(" +
                            "id INTEGER PRIMARY KEY, name text NOT NULL, category_id INTEGER," +
                            "FOREIGN KEY(category_id)" +
                            "REFERENCES category(id) " +
                        ")"
        );

        // create table - paymentMethod
        db.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.PAYMENTTYPE_NAME +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL,drawable_id INTEGER DEFAULT 0)"
        );

        // create table - expenseTracker
        db.execSQL(
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

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    // false - no record inserted
    // true - record inserted successfully
    public boolean insertExpenseSettings(ExpenseSettings expenseSettings)
    {
        for(PaymentType l : expenseSettings.getPaymentMethod()) {
            insertPaymentType(l);
        }

        for(Category l : expenseSettings.getCategory()) {
            // update subcategory
            insertCategory(l);
        }

        return true;
    }

    public static void insertCategory(Category cat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", cat.getName());
        contentValues.put("color_id", cat.getColorId());
        DBManager dbManager = DBManager.getDBManagerInstance();
        boolean ret = dbManager.insert(contentValues, DatabaseDetails.CATEGORY_NAME);
        if(false == ret)
        {
            throw new RuntimeException("Failed to insert data into database");
        }

        int catID = Integer.parseInt(getData(DatabaseDetails.CATEGORY_NAME, "name = '" + cat.getName() + "' ", "id"));
        for(SubCategory sCat: cat.getSubCategories())
        {
            sCat.setCategoryId(catID);
            insertSubCategory(sCat);
        }
    }

    public static void insertSubCategory(SubCategory scat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", scat.getName());
        contentValues.put("category_id", scat.getCategoryId());
        DBManager dbManager = DBManager.getDBManagerInstance();
        boolean ret = dbManager.insert(contentValues, DatabaseDetails.SUBCATEGORY_NAME);
        if(false == ret)
        {
            throw new RuntimeException("Failed to insert data into database");
        }
    }

    public static void insertPaymentType(PaymentType p)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("drawable_id", p.getDrawableId());
        DBManager dbManager = DBManager.getDBManagerInstance();
        dbManager.insert(contentValues, DatabaseDetails.PAYMENTTYPE_NAME);
    }

    public static void updatePaymentType(PaymentType p)
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        String condition = "id = " + p.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("drawable_id", p.getDrawableId());
        dbManager.update(DatabaseDetails.PAYMENTTYPE_NAME, contentValues, condition);
    }

    public static void updateCategory(Category c)
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        String condition = "id = '" + c.getId() + "'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", c.getName());
        contentValues.put("color_id", c.getColorId());
        dbManager.update(DatabaseDetails.CATEGORY_NAME, contentValues, condition);
    }

    public static void updateSubCategory(SubCategory s)
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        String condition = "id = " + s.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", s.getName());
        contentValues.put("category_id", s.getCategoryId());
        dbManager.update(DatabaseDetails.SUBCATEGORY_NAME, contentValues ,condition);
    }

    public static void deletePaymentType(PaymentType p)
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        String condition = "id = '" + p.getId() +"'";
        dbManager.delete(DatabaseDetails.PAYMENTTYPE_NAME, condition);
    }

    public static void deleteCategory(Category c)
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        int categoryId = c.getId();
        for(SubCategory sCat: c.getSubCategories())
        {
            sCat.setCategoryId(categoryId);
            deleteSubCategory(sCat);
        }
        String condition = "id = '" + c.getId() +"'";
        dbManager.delete(DatabaseDetails.CATEGORY_NAME, condition);
    }

    public static void deleteSubCategory(SubCategory s)
    {
        DBManager dbManager = DBManager.getDBManagerInstance();
        String condition = "id = '" + s.getId() +"'";
        dbManager.delete(DatabaseDetails.SUBCATEGORY_NAME, condition);
    }

    public boolean insert(ContentValues contentValues, String tableName)
    {
        getDatabaseInstance().insert(tableName, null, contentValues);
        return true;
    }

    public int delete(String tableName, String condition) {
        DBManager db = getDBManagerInstance();
        int ret =  db.getDatabaseInstance().delete(tableName, condition, null);
        Log.d("DATABASE_LOG", "delete: exiting" );
        return ret;
    }

    public int update(String tableName, ContentValues cv, String condition) {
        DBManager db = getDBManagerInstance();
        int ret =  db.getDatabaseInstance().update(tableName, cv, condition, null);
        Log.d("DATABASE_LOG", "update: exiting");
        return ret;
    }

    public static Cursor getData(String tableName, String condition) {
        try {
            DBManager db = getDBManagerInstance();
            String query = "select * from " + tableName;
            query += (null == condition) ? "" : " where " + condition;
            Log.d("DATABASE_LOG", "getData: query = " + query);
            Cursor cursor = db.getDatabaseInstance().rawQuery(query,
                    null);
            if (null != cursor)
                cursor.moveToFirst();

            return cursor;
        }
        catch(Exception e) {
            return null;
        }
    }

    public static ArrayList<Category> getCategoryData() {
        DBManager db = getDBManagerInstance();
        ArrayList<Category> data = new ArrayList<>();
        String query = "select * from "+ DatabaseDetails.CATEGORY_NAME;
        Cursor cursor =  db.getDatabaseInstance().rawQuery( query,
                null );
        if(null != cursor)
            cursor.moveToFirst();

        ArrayList<SubCategory> subCategories = db.getSubCategoryData();

        do {
            ArrayList<SubCategory> subCategoriesByCategory = new ArrayList<>();
            int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
            String name = cursor.getString(cursor.getColumnIndex("name"));

            for (SubCategory subCat: subCategories) {
                if(subCat.getCategoryId() == id)
                {
                    subCategoriesByCategory.add(new SubCategory(subCat.getId(), subCat.getName(), subCat.getCategoryId(), subCat.getDrawableId()));
                }
            }
            data.add(new Category(id, name, R.color.categoryYellow,subCategoriesByCategory));
        } while(cursor.moveToNext());
        return  data;
    }

    public static ArrayList<SubCategory> getSubCategoryData() {
        DBManager db = getDBManagerInstance();
        ArrayList<SubCategory>  data = new ArrayList<>();
        String query = "select * from "+ DatabaseDetails.SUBCATEGORY_NAME;
        Cursor cursor =  db.getDatabaseInstance().rawQuery( query,
                null );
        if(null != cursor)
            cursor.moveToFirst();

        do {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String categoryId = cursor.getString(cursor.getColumnIndex("category_id"));
            SubCategory obj = new SubCategory(Integer.parseInt(id), name, Integer.parseInt(categoryId), 0);
            data.add(obj);
        } while(cursor.moveToNext());
        return data;
    }

    public static ArrayList<PaymentType> getPaymentData() {
        DBManager db = getDBManagerInstance();
        ArrayList<PaymentType> data = new ArrayList<>();
        String query = "select * from "+ DatabaseDetails.PAYMENTTYPE_NAME;
        Cursor cursor =  db.getDatabaseInstance().rawQuery( query,
                null );
        if(null != cursor)
            cursor.moveToFirst();

        do {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            PaymentType obj = new PaymentType(Integer.parseInt(id), name, 0);
            data.add(obj);
        } while(cursor.moveToNext());
        return data;
    }

    private static String getData(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static String getData(String tableName, String condition, String columnName) {
        Cursor cursor = DBManager.getData(tableName, condition);
        return getData(cursor, columnName);
    }

}
