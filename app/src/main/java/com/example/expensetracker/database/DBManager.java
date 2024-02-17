package com.example.expensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.expensetracker.Settings;
import com.example.expensetracker.R;
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
                        "(id INTEGER PRIMARY KEY, name TEXT NOT NULL,drawable_id INTEGER DEFAULT 0)"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.USERS +
                        "(id INTEGER PRIMARY KEY, name TEXT NOT NULL,drawable_id INTEGER DEFAULT 0)"
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
                            "subcategory_id INTEGER NOT NULL," +
                            "paymentmethod_id INTEGER, " +
                            "date_time TEXT, " +
                            "isShared_expense INTEGER, " +
                            "FOREIGN KEY(subcategory_id) REFERENCES "+ DatabaseDetails.SUBCATEGORY_EXPENSE +"(id)," +
                            "FOREIGN KEY(paymentmethod_id) REFERENCES "+ DatabaseDetails.PAYMENT_TYPE +"(id)" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS  " + DatabaseDetails.EXPENSE_SHARED +
                        "( " +
                        "id INTEGER PRIMARY KEY, " +
                        "expenseentries_id INTEGER NOT NULL, " +
                        "user_id INTEGER, " +
                        "value INTEGER, " +
                        "FOREIGN KEY(expenseentries_id) REFERENCES "+ DatabaseDetails.EXPENSE_ENTRIES +"(id)," +
                        "FOREIGN KEY(user_id) REFERENCES "+ DatabaseDetails.USERS +"(id)" +
                        ")"
        );

        sqLiteDatabase.execSQL(
                "create table IF NOT EXISTS " + DatabaseDetails.INCOME_ENTRIES +
                        "(" +
                        "id INTEGER PRIMARY KEY, " +
                        "name TEXT NOT NULL," +
                        "value REAL NOT NULL, " +
                        "subcategory_id INTEGER NOT NULL," +
                        "paymentmethod_id INTEGER, " +
                        "date_time TEXT, " +
                        "FOREIGN KEY(subcategory_id) REFERENCES "+ DatabaseDetails.SUBCATEGORY_INCOME +"(id)," +
                        "FOREIGN KEY(paymentmethod_id) REFERENCES "+ DatabaseDetails.PAYMENT_TYPE +"(id)" +
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
        this.update(DatabaseDetails.CATEGORY_EXPENSE, contentValues, condition);
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
        this.update(DatabaseDetails.CATEGORY_INCOME, contentValues, condition);
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

    public void insertUser(User u)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", u.getName());
        contentValues.put("drawable_id", u.getDrawableId());
        this.insert(contentValues, DatabaseDetails.USERS);
    }

    public void updateUser(User u)
    {
        String condition = "id = " + u.getId();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", u.getName());
        contentValues.put("drawable_id", u.getDrawableId());
        this.update(DatabaseDetails.USERS, contentValues, condition);
    }

    public void deleteUser(User u)
    {
        String condition = "id = '" + u.getId() +"'";
        this.delete(DatabaseDetails.USERS, condition);
    }

    public void insertSharedUserEntries(Entry.SharedUser sharedUser, long expenseEntriesId)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("expense_entries_id", expenseEntriesId);
        contentValues.put("user_id", sharedUser.getUser().getId());
        contentValues.put("value", sharedUser.getValue());
        this.insert(contentValues, DatabaseDetails.EXPENSE_SHARED);
    }

     public ArrayList<Entry.SharedUser> getSharedUserEntries(long expenseID)
     {
         ArrayList<Entry.SharedUser> data = new ArrayList<>();
         String query = "select id, user_id, "
                        + DatabaseDetails.USERS+ ".name user_name,  " + " value from "
                        + DatabaseDetails.EXPENSE_SHARED
                        + " JOIN  " + DatabaseDetails.USERS
                        + " ON " + DatabaseDetails.EXPENSE_SHARED + ".user_id = " + DatabaseDetails.USERS + ".id "
                        + " where expense_entries_id = " + expenseID;
         try(Cursor cursor =  sqLiteDatabase.rawQuery(query,
                 null)) {
             if (null != cursor)
                 cursor.moveToFirst();
             do {
                 assert cursor != null;
                 long id = cursor.getLong(0);
                 long user_id = cursor.getLong(1);
                 String name = cursor.getString(2);
                 int value = cursor.getInt(3);
                 Entry.SharedUser obj = new Entry.SharedUser(new User(user_id,name), id, value);
                 data.add(obj);
             } while (cursor.moveToNext());
         }
         catch (Exception e)
         {
             e.printStackTrace();
         }
         return data;
     }

    public void insertExpenseEntries(Entry entry)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", entry.getName());
        contentValues.put("value", entry.getValue());
        contentValues.put("subCategory_id", entry.getSubCategoryId());
        contentValues.put("paymentMethod_id", entry.getPaymentId());
		contentValues.put("date_time", entry.getDateAndTime().toString());
        contentValues.put("isShared_expense", entry.isShared());

        long id = this.insert(contentValues, DatabaseDetails.EXPENSE_ENTRIES);
        if(id == -1)
        {
            throw  new RuntimeException();
        }
        entry.getSharedUsersList().forEach(sharedUser ->
        {
            insertSharedUserEntries(sharedUser, id);
        });
    }

    public void insertIncomeEntries(Entry entry)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", entry.getName());
        contentValues.put("value", entry.getValue());
        contentValues.put("category_id", entry.getCategoryId());
        contentValues.put("subCategory_id", entry.getSubCategoryId());
        contentValues.put("paymentMethod_id", entry.getPaymentId());
        contentValues.put("date_time", entry.getDateAndTime().toString());

        long id = this.insert(contentValues, DatabaseDetails.INCOME_ENTRIES);
        if(id == -1)
        {
            throw  new RuntimeException();
        }
    }

    public ArrayList<Entry> getIncomeEntries() {
        ArrayList<Entry> data = new ArrayList<>();
        String query = "select id, name, value, category_id, subCategory_id, paymentMethod_id, date,time from "+ DatabaseDetails.INCOME_ENTRIES;
        try(Cursor cursor =  sqLiteDatabase.rawQuery(query,
                null)) {
            if (null != cursor)
                cursor.moveToFirst();
            do {
                assert cursor != null;
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                int value = cursor.getInt(2);
                long category_id = cursor.getLong(3);
                long subCategory_id = cursor.getLong(4);
                long paymentMethod_id = cursor.getLong(5);
                Entry entry = new Entry(id, name, value, category_id, subCategory_id, paymentMethod_id, new Date(),new Date());
                data.add(entry);
            } while (cursor.moveToNext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public void insertTransferEntries(int sender_user_id, int receiver_user_id, int value)
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

    public long insert(ContentValues contentValues, String tableName)
    {
        return sqLiteDatabase.insert(tableName, null, contentValues);
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
