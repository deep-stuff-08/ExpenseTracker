package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.expensetracker.R;
import com.example.expensetracker.Settings;
import com.example.expensetracker.pojo.Category;
import com.example.expensetracker.pojo.Entry;
import com.example.expensetracker.pojo.PaymentType;
import com.example.expensetracker.pojo.SubCategory;
import com.example.expensetracker.pojo.User;

import java.util.ArrayList;
import java.util.Date;

public class DBManager{
    private static DBManager dbManager = null;

    private SQLiteDatabase sqLiteDatabase ;

    private DBManager()
    {

    }

    private static boolean isDatabaseCreated(Context conTEXT)
    {
        try {
           SQLiteDatabase.openDatabase(conTEXT.getDatabasePath(DatabaseDetails.DATABASE_NAME).getPath(), null, SQLiteDatabase.OPEN_READONLY).close();
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
                        "(id INTEGER PRIMARY KEY, name TEXT NOT NULL,color_id INTEGER DEFAULT 0)"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.USERS +
                        "(id INTEGER PRIMARY KEY, name TEXT NOT NULL,color_id INTEGER DEFAULT 0)"
        );

        // TABLES AS PER Expense/Income TYPE
        // create table - category
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.CATEGORY_EXPENSE +
                        "(id INTEGER PRIMARY KEY, name TEXT NOT NULL, color_id INTEGER)"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.CATEGORY_INCOME +
                        "(id INTEGER PRIMARY KEY, name TEXT NOT NULL, color_id INTEGER)"
        );

        // create table - subCategory
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.SUBCATEGORY_EXPENSE +
                        "(" +
                            "id INTEGER PRIMARY KEY, name text NOT NULL, category_id INTEGER," +
                            "FOREIGN KEY(category_id) REFERENCES "+ DatabaseDetails.CATEGORY_EXPENSE +"(id)" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.SUBCATEGORY_INCOME +
                        "(" +
                        "id INTEGER PRIMARY KEY, name text NOT NULL, category_id INTEGER," +
                        "FOREIGN KEY(category_id) REFERENCES "+ DatabaseDetails.CATEGORY_INCOME +"(id) " +
                        ")"
        );

        // create table - expenseTracker
        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.EXPENSE_ENTRIES +
                        "(" +
                            "id INTEGER PRIMARY KEY, " +
                            "name TEXT NOT NULL," +
                            "value REAL NOT NULL, " +
                            "subCategory_id INTEGER NOT NULL," +
                            "paymentMethod_id INTEGER, " +
                            "date_time TEXT, " +
                            "isShared INTEGER, " +
                            "FOREIGN KEY(subCategory_id) REFERENCES "+ DatabaseDetails.SUBCATEGORY_EXPENSE +"(id)," +
                            "FOREIGN KEY(paymentMethod_id) REFERENCES "+ DatabaseDetails.PAYMENT_TYPE +"(id)" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.EXPENSE_SHARED +
                        "( " +
                        "id INTEGER PRIMARY KEY, " +
                        "expenseEntries_id INTEGER NOT NULL, " +
                        "user_id INTEGER, " +
                        "value INTEGER, " +
                        "FOREIGN KEY(expenseEntries_id) REFERENCES "+ DatabaseDetails.EXPENSE_ENTRIES +"(id)," +
                        "FOREIGN KEY(user_id) REFERENCES "+ DatabaseDetails.USERS +"(id)" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.INCOME_ENTRIES +
                        "(" +
                        "id INTEGER PRIMARY KEY, " +
                        "name TEXT NOT NULL," +
                        "value REAL NOT NULL, " +
                        "subCategory_id INTEGER NOT NULL," +
                        "paymentMethod_id INTEGER, " +
                        "date_time TEXT, " +
                        "FOREIGN KEY(subCategory_id) REFERENCES "+ DatabaseDetails.SUBCATEGORY_INCOME +"(id)," +
                        "FOREIGN KEY(paymentMethod_id) REFERENCES "+ DatabaseDetails.PAYMENT_TYPE +"(id)" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.TRANSFER +
                        "(" +
                        "id INTEGER PRIMARY KEY, " +
                        "sender_id INTEGER NOT NULL, " +
                        "receiver_id INTEGER NOT NULL, " +
                        "value INTEGER NOT NULL, " +
                        "FOREIGN KEY(sender_id) REFERENCES "+  DatabaseDetails.USERS +"(id), " +
                        "FOREIGN KEY(receiver_id) REFERENCES "+  DatabaseDetails.USERS +"(id)" +
                        ")"
        );
    }

    public void insertSettings(Settings settings)
    {
        settings.getPaymentMethod().forEach(this::insertPaymentType);
        settings.getExpenseCategory().forEach(this::insertExpenseCategory);
        settings.getIncomeCategory().forEach(this::insertIncomeCategory);
        settings.getUsers().forEach(this::insertUser);
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
        long catID = this.insert(contentValues, DatabaseDetails.CATEGORY_EXPENSE);
        if(catID == -1) {
            throw new RuntimeException("Failed to insert data into database");
        }
        for(SubCategory sCat: cat.getSubCategories())
        {
            sCat.setCategoryId((int)catID);
            insertExpenseSubCategory(sCat);
        }
    }

    public void updateExpenseCategory(Category c)
    {
        String condition = "id = '" + c.getId() + "'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", c.getName());
        contentValues.put("color_id", c.getColorId());
        if(this.update(DatabaseDetails.CATEGORY_EXPENSE, contentValues, condition) < 1) {
            throw new RuntimeException("No Rows Updated");
        }
    }

    public void deleteExpenseCategory(Category c)
    {
        long categoryId = c.getId();
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
        long catID = this.insert(contentValues, DatabaseDetails.CATEGORY_INCOME);
        if(catID == -1) {
            throw new RuntimeException("Failed to insert data into database");
        }
        for(SubCategory sCat: cat.getSubCategories())
        {
            sCat.setCategoryId((int)catID);
            insertIncomeSubCategory(sCat);
        }
    }

    public void updateIncomeCategory(Category c)
    {
        String condition = "id = '" + c.getId() + "'";
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", c.getName());
        contentValues.put("color_id", c.getColorId());
        if(this.update(DatabaseDetails.CATEGORY_INCOME, contentValues, condition) < 1) {
            throw new RuntimeException("No Entry Updated");
        }
    }

    public void deleteIncomeCategory(Category c)
    {
        long categoryId = c.getId();
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
        long ret = this.insert(contentValues, DatabaseDetails.SUBCATEGORY_EXPENSE);
        if(ret == -1)
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
        if(this.update(DatabaseDetails.SUBCATEGORY_EXPENSE, contentValues ,condition) < 1) {
            throw new RuntimeException("No Rows Updated");
        }
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
        long ret = this.insert(contentValues, DatabaseDetails.SUBCATEGORY_INCOME);
        if(ret == -1)
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
        if(this.update(DatabaseDetails.SUBCATEGORY_INCOME, contentValues ,condition) < 1) {
            throw new RuntimeException("No Rows Updated");
        }
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
        contentValues.put("color_id", p.getDrawableId());
        this.insert(contentValues, DatabaseDetails.PAYMENT_TYPE);
    }

    public void updatePaymentType(PaymentType p)
    {
        String condition = "id = " + p.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", p.getName());
        contentValues.put("color_id", p.getDrawableId());
        if(this.update(DatabaseDetails.PAYMENT_TYPE, contentValues, condition) < 1) {
            throw new RuntimeException("No Rows Updated");
        }
    }

    public void deletePaymentType(PaymentType p)
    {
        String condition = "id = '" + p.getId() +"'";
        this.delete(DatabaseDetails.PAYMENT_TYPE, condition);
    }

    public ArrayList<User> getUserData() {
        ArrayList<User> data = new ArrayList<>();
        String query = "select id, name from "+ DatabaseDetails.USERS;
        try(Cursor cursor =  sqLiteDatabase.rawQuery(query,
                null)) {
            if (null != cursor)
                cursor.moveToFirst();

            do {
                assert cursor != null;
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                User obj = new User(Integer.parseInt(id), name);
                data.add(obj);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public long insertUser(User u)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", u.getName());
        contentValues.put("color_id", u.getDrawableId());
        return this.insert(contentValues, DatabaseDetails.USERS);
    }

    public void updateUser(User u)
    {
        String condition = "id = " + u.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", u.getName());
        contentValues.put("color_id", u.getDrawableId());
        if(this.update(DatabaseDetails.USERS, contentValues, condition) < 1) {
            throw new RuntimeException("No Rows Updated");
        }
    }

    public void deleteUser(User u)
    {
        String condition = "id = '" + u.getId() +"'";
        this.delete(DatabaseDetails.USERS, condition);
    }

    public void insertSharedUserEntries(Entry.SharedUser sharedUser, long expenseEntriesId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("expenseEntries_id", expenseEntriesId);
        contentValues.put("user_id", sharedUser.getUser().getId());
        contentValues.put("value", sharedUser.getValue());
        this.insert(contentValues, DatabaseDetails.EXPENSE_SHARED);
    }

    public void insertExpenseEntries(Entry entry)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", entry.getName());
        contentValues.put("value", entry.getValue());
        contentValues.put("subCategory_id", entry.getSubCategoryId());
        contentValues.put("paymentMethod_id", entry.getPaymentId());
        contentValues.put("date_time", entry.getDateAndTime().toString());
        contentValues.put("isShared", entry.isShared());

        long id = this.insert(contentValues, DatabaseDetails.EXPENSE_ENTRIES);
        if(id == -1)
        {
            throw new RuntimeException();
        }
        entry.getSharedUsersList().forEach(sharedUser -> insertSharedUserEntries(sharedUser, id));
    }

    public void insertIncomeEntries(Entry entry)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", entry.getName());
        contentValues.put("value", entry.getValue());
        contentValues.put("subCategory_id", entry.getSubCategoryId());
        contentValues.put("paymentMethod_id", entry.getPaymentId());
        contentValues.put("date_time", entry.getDateAndTime().toString());

        long id = this.insert(contentValues, DatabaseDetails.INCOME_ENTRIES);
        if(id == -1)
        {
            throw  new RuntimeException();
        }
    }

    public ArrayList<Entry.SharedUser> getSharedUserEntries(long expenseID)
    {
        ArrayList<Entry.SharedUser> data = new ArrayList<>();
        String query = "select " + DatabaseDetails.EXPENSE_SHARED + ".id, user_id, "
                + DatabaseDetails.USERS+ ".name user_name,  " + " value from "
                + DatabaseDetails.EXPENSE_SHARED
                + " JOIN  " + DatabaseDetails.USERS
                + " ON " + DatabaseDetails.EXPENSE_SHARED + ".user_id = " + DatabaseDetails.USERS + ".id "
                + " where expenseEntries_id = " + expenseID;
        try(Cursor cursor =  sqLiteDatabase.rawQuery(query,
                null)) {
            if (null == cursor || 0 == cursor.getCount() )
                return data;

            cursor.moveToFirst();
            do {
                long user_id = cursor.getLong(1);
                String user_name = cursor.getString(2);
                int shared_value = cursor.getInt(3);
                Entry.SharedUser obj = new Entry.SharedUser(new User(user_id,user_name), shared_value);
                data.add(obj);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<Entry> getExpenseEntries() {
        ArrayList<Entry> data = new ArrayList<>();
        String query = "select "+ DatabaseDetails.EXPENSE_ENTRIES +".id, " + DatabaseDetails.EXPENSE_ENTRIES +".name, "
                +DatabaseDetails.EXPENSE_ENTRIES+".value, "
                +DatabaseDetails.SUBCATEGORY_EXPENSE + ".category_id as category_id, " + DatabaseDetails.CATEGORY_EXPENSE + ".name as category_name, "
                +DatabaseDetails.EXPENSE_ENTRIES +".subCategory_id as subCategory_id," + DatabaseDetails.SUBCATEGORY_EXPENSE + ".name as subCategory_name, "
                +DatabaseDetails.EXPENSE_ENTRIES+".paymentMethod_id as paymentMethod_id," + DatabaseDetails.PAYMENT_TYPE+".name as payment_name, "
                +"date_time FROM "+ DatabaseDetails.EXPENSE_ENTRIES
                +" JOIN  " + DatabaseDetails.SUBCATEGORY_EXPENSE
                +" ON " + DatabaseDetails.EXPENSE_ENTRIES + ".subCategory_id = " + DatabaseDetails.SUBCATEGORY_EXPENSE + ".id"
                +" JOIN " + DatabaseDetails.PAYMENT_TYPE
                +" ON " + DatabaseDetails.EXPENSE_ENTRIES + ".paymentMethod_id = " + DatabaseDetails.PAYMENT_TYPE + ".id"
                +" JOIN " + DatabaseDetails.CATEGORY_EXPENSE
                +" ON " + DatabaseDetails.SUBCATEGORY_EXPENSE + ".category_id = " + DatabaseDetails.CATEGORY_EXPENSE + ".id"
                ;

        try(Cursor cursor =  sqLiteDatabase.rawQuery(query, null)) {
            if (null == cursor || 0 == cursor.getCount() )
                return data;
            cursor.moveToFirst();
            do {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                int value = -cursor.getInt(2);
                long category_id = cursor.getLong(3);
                long subCategory_id = cursor.getLong(5);
                long paymentMethod_id = cursor.getLong(7);

                ArrayList<Entry.SharedUser> sharedUsersList = getSharedUserEntries(id);
                Entry entry = new Entry(id, name, value, category_id, subCategory_id, paymentMethod_id, new Date(), new Date(), sharedUsersList);
                data.add(entry);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }
    public ArrayList<Entry> getIncomeEntries() {
        ArrayList<Entry> data = new ArrayList<>();
        String query = "select "+ DatabaseDetails.INCOME_ENTRIES +".id, " + DatabaseDetails.INCOME_ENTRIES +".name, "
                +DatabaseDetails.INCOME_ENTRIES+".value, "
                +DatabaseDetails.SUBCATEGORY_INCOME + ".category_id as category_id, " + DatabaseDetails.CATEGORY_INCOME + ".name as category_name, "
                +DatabaseDetails.INCOME_ENTRIES +".subCategory_id as subCategory_id," + DatabaseDetails.SUBCATEGORY_INCOME + ".name as subCategory_name, "
                +DatabaseDetails.INCOME_ENTRIES+".paymentMethod_id as paymentMethod_id," + DatabaseDetails.PAYMENT_TYPE+".name as payment_name, "
                +"date_time FROM "+ DatabaseDetails.INCOME_ENTRIES
                +" JOIN  " + DatabaseDetails.SUBCATEGORY_INCOME
                +" ON " + DatabaseDetails.INCOME_ENTRIES + ".subCategory_id = " + DatabaseDetails.SUBCATEGORY_INCOME + ".id"
                +" JOIN " + DatabaseDetails.PAYMENT_TYPE
                +" ON " + DatabaseDetails.INCOME_ENTRIES + ".paymentMethod_id = " + DatabaseDetails.PAYMENT_TYPE + ".id"
                +" JOIN " + DatabaseDetails.CATEGORY_INCOME
                +" ON " + DatabaseDetails.SUBCATEGORY_INCOME + ".category_id = " + DatabaseDetails.CATEGORY_INCOME + ".id"
                ;
        try(Cursor cursor =  sqLiteDatabase.rawQuery(query, null)) {
            if (null == cursor || 0 == cursor.getCount() )
                return data;
            cursor.moveToFirst();
            do {
                    long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    int value = cursor.getInt(2);
                    long category_id = cursor.getLong(3);
                    long subCategory_id = cursor.getLong(5);
                    long paymentMethod_id = cursor.getLong(7);
                    Entry entry = new Entry(id, name, value, category_id, subCategory_id, paymentMethod_id, new Date(), new Date());
                    data.add(entry);
            } while (cursor.moveToNext());
            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return  data;
        }
    }

    public void insertTransferEntries(long sender_user_id, long receiver_user_id, int value)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("sender_id", sender_user_id);
        contentValues.put("receiver_id", receiver_user_id);
        contentValues.put("value", value);

        long id = this.insert(contentValues, DatabaseDetails.TRANSFER);
        if(id == -1)
        {
            throw  new RuntimeException();
        }
    }

    private long insert(ContentValues contentValues, String tableName)
    {
        return sqLiteDatabase.insert(tableName, null, contentValues);
    }

    private void delete(String tableName, String condition) {
        sqLiteDatabase.delete(tableName, condition, null);
        Log.d("DATABASE_LOG", "delete: exiting" );
    }

    private int update(String tableName, ContentValues cv, String condition) {
        int ret =  sqLiteDatabase.update(tableName, cv, condition, null);
        Log.d("DATABASE_LOG", "update: exiting");
        return ret;
    }

    /*public Cursor getData(String columnName, String tableName, String condition) {
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
    }*/
}
