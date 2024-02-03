package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.expensetracker.Settings;
import com.example.expensetracker.R;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.User;

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
            dbManager.insertSettings(Settings.createWithDefaultParameters());
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

    public void onCreateSetup()
    {
        Log.d("DATABASE_LOG", "onCreate: called");

        // COMMON TABLES
        // create table - paymentMethod
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.PAYMENT_TYPE +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL,drawable_id INTEGER DEFAULT 0)"
        );

        // TABLES AS PER Expense/Income TYPE
        // create table - category
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.CATEGORY_EXPENSE +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL, color_id INTEGER)"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.CATEGORY_INCOME +
                        "(id INTEGER PRIMARY KEY, name text NOT NULL, color_id INTEGER)"
        );

        // create table - subCategory
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.SUBCATEGORY_EXPENSE +
                        "(" +
                            "id INTEGER PRIMARY KEY, name text NOT NULL, category_id INTEGER," +
                            "FOREIGN KEY(category_id)" +
                            "REFERENCES category(id) " +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.SUBCATEGORY_INCOME +
                        "(" +
                        "id INTEGER PRIMARY KEY, name text NOT NULL, category_id INTEGER," +
                        "FOREIGN KEY(category_id)" +
                        "REFERENCES category(id) " +
                        ")"
        );

        // create table - expenseTracker
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.EXPENSE_ENTRIES +
                        "(" +
                            "id INTEGER PRIMARY KEY, name text NOT NULL," +
                            "value REAL NOT NULL, category_id INTEGER NOT NULL," +
                            "subCategory_id INTEGER NOT NULL," +
                            "paymentMethod_id INTEGER, dateTime TEXT, isSharedExpense INTEGER" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.INCOME_ENTRIES +
                        "(" +
                        "id INTEGER PRIMARY KEY, name text NOT NULL," +
                        "value REAL NOT NULL, category_id INTEGER NOT NULL," +
                        "subCategory_id INTEGER NOT NULL," +
                        "paymentMethod_id INTEGER, dateTime TEXT, isSharedExpense INTEGER" +
                        ")"
        );

        Log.d("DATABASE_LOG", "onCreate: exit");
    }

    public void insertSettings(Settings settings)
    {
        for(PaymentType l : settings.getPaymentMethod()) {
            insertPaymentType(l);
        }

        for(Category l : settings.getExpenseCategory()) {
            // insert subcategory is called in the insertCategory
            insertExpenseCategory(l);
        }

        for(Category l : settings.getIncomeCategory()) {
            // insert subcategory is called in the insertCategory
            insertIncomeCategory(l);
        }
    }

    public ArrayList<Category> getExpenseCategoryData() {
        ArrayList<Category> data = new ArrayList<>();
        String query = "select id, name, color_id from "+ DatabaseDetails.CATEGORY_EXPENSE;
        try(Cursor cursor =  sqLiteDatabase.rawQuery( query,null )) {
            if(null == cursor)
            {
                throw new RuntimeException("cursor is null");
            }

            ArrayList<SubCategory> subCategories = this.getExpenseSubCategoryData();
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

    public void insertExpenseCategory(Category cat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", cat.getName());
        contentValues.put("color_id", cat.getColorId());
        boolean ret = this.insert(contentValues, DatabaseDetails.CATEGORY_EXPENSE);
        if(!ret) {
            throw new RuntimeException("Failed to insert data into database");
        }

        Cursor cursor = getData("id", DatabaseDetails.CATEGORY_EXPENSE, "name = '" + cat.getName() + "' ");
        assert cursor != null;
        int catID = Integer.parseInt(cursor.getString(0));
        for(SubCategory sCat: cat.getSubCategories())
        {
            sCat.setCategoryId(catID);
            insertExpenseSubCategory(sCat);
        }
    }

    public void updateExpenseCategory(Category c)
    {
        String condition = "id = '" + c.getId() + "'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", c.getName());
        contentValues.put("color_id", c.getColorId());
        this.update(DatabaseDetails.CATEGORY_EXPENSE, contentValues, condition);
    }

    public void deleteExpenseCategory(Category c)
    {
        int categoryId = c.getId();
        for(SubCategory sCat: c.getSubCategories())
        {
            sCat.setCategoryId(categoryId);
            deleteExpenseSubCategory(sCat);
        }
        String condition = "id = '" + c.getId() +"'";
        this.delete(DatabaseDetails.CATEGORY_EXPENSE, condition);
    }

    public ArrayList<Category> getIncomeCategoryData() {
        ArrayList<Category> data = new ArrayList<>();
        String query = "select id, name, color_id from "+ DatabaseDetails.CATEGORY_INCOME;
        try(Cursor cursor =  sqLiteDatabase.rawQuery( query,null )) {
            if(null == cursor)
            {
                throw new RuntimeException("cursor is null");
            }

            ArrayList<SubCategory> subCategories = this.getIncomeSubCategoryData();
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

    public void insertIncomeCategory(Category cat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", cat.getName());
        contentValues.put("color_id", cat.getColorId());
        boolean ret = this.insert(contentValues, DatabaseDetails.CATEGORY_INCOME);
        if(!ret) {
            throw new RuntimeException("Failed to insert data into database");
        }

        Cursor cursor = getData("id", DatabaseDetails.CATEGORY_INCOME, "name = '" + cat.getName() + "' ");
        assert cursor != null;
        int catID = Integer.parseInt(cursor.getString(0));
        for(SubCategory sCat: cat.getSubCategories())
        {
            sCat.setCategoryId(catID);
            insertIncomeSubCategory(sCat);
        }
    }

    public void updateIncomeCategory(Category c)
    {
        String condition = "id = '" + c.getId() + "'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", c.getName());
        contentValues.put("color_id", c.getColorId());
        this.update(DatabaseDetails.CATEGORY_INCOME, contentValues, condition);
    }

    public void deleteIncomeCategory(Category c)
    {
        int categoryId = c.getId();
        for(SubCategory sCat: c.getSubCategories())
        {
            sCat.setCategoryId(categoryId);
            deleteExpenseSubCategory(sCat);
        }
        String condition = "id = '" + c.getId() +"'";
        this.delete(DatabaseDetails.CATEGORY_INCOME, condition);
    }

    public ArrayList<SubCategory> getExpenseSubCategoryData() {
        ArrayList<SubCategory>  data = new ArrayList<>();
        String query = "select id, name, category_id from "+ DatabaseDetails.SUBCATEGORY_EXPENSE;
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

    public void insertExpenseSubCategory(SubCategory scat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", scat.getName());
        contentValues.put("category_id", scat.getCategoryId());
        boolean ret = this.insert(contentValues, DatabaseDetails.SUBCATEGORY_EXPENSE);
        if(!ret)
        {
            throw new RuntimeException("Failed to insert data into database");
        }
    }

    public void updateExpenseSubCategory(SubCategory s)
    {
        String condition = "id = " + s.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", s.getName());
        contentValues.put("category_id", s.getCategoryId());
        this.update(DatabaseDetails.SUBCATEGORY_EXPENSE, contentValues ,condition);
    }

    public void deleteExpenseSubCategory(SubCategory s)
    {
        String condition = "id = '" + s.getId() +"'";
        this.delete(DatabaseDetails.SUBCATEGORY_EXPENSE, condition);
    }

    public ArrayList<SubCategory> getIncomeSubCategoryData() {
        ArrayList<SubCategory>  data = new ArrayList<>();
        String query = "select id, name, category_id from "+ DatabaseDetails.SUBCATEGORY_INCOME;
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

    public void insertIncomeSubCategory(SubCategory scat)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", scat.getName());
        contentValues.put("category_id", scat.getCategoryId());
        boolean ret = this.insert(contentValues, DatabaseDetails.SUBCATEGORY_INCOME);
        if(!ret)
        {
            throw new RuntimeException("Failed to insert data into database");
        }
    }

    public void updateIncomeSubCategory(SubCategory s)
    {
        String condition = "id = " + s.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", s.getName());
        contentValues.put("category_id", s.getCategoryId());
        this.update(DatabaseDetails.SUBCATEGORY_INCOME, contentValues ,condition);
    }

    public void deleteIncomeSubCategory(SubCategory s)
    {
        String condition = "id = '" + s.getId() +"'";
        this.delete(DatabaseDetails.SUBCATEGORY_INCOME, condition);
    }

    public ArrayList<PaymentType> getPaymentData() {
        ArrayList<PaymentType> data = new ArrayList<>();
        String query = "select id, name from "+ DatabaseDetails.PAYMENT_TYPE;
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

    public void insertPaymentType(PaymentType p)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("drawable_id", p.getDrawableId());
        this.insert(contentValues, DatabaseDetails.PAYMENT_TYPE);
    }

    public void updatePaymentType(PaymentType p)
    {
        String condition = "id = " + p.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("drawable_id", p.getDrawableId());
        this.update(DatabaseDetails.PAYMENT_TYPE, contentValues, condition);
    }

    public void deletePaymentType(PaymentType p)
    {
        String condition = "id = '" + p.getId() +"'";
        this.delete(DatabaseDetails.PAYMENT_TYPE, condition);
    }

    public ArrayList<User> getUserData() {
        ArrayList<User> data = new ArrayList<>();
        data.add(new User("Hrituja"));
        data.add(new User("Deep"));
        data.add(new User("Darshan"));
        data.add(new User("Rushikesh"));
        return data;
    }

    public void insertUser(User u)
    {

    }

    public void updateUser(User u)
    {

    }

    public void deleteUser(User u)
    {

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
