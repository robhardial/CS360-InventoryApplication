package com.zybooks.roberthardial_project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_ITEM_NAME = "item_name";
    private static final String COLUMN_ITEM_QUANTITY = "item_quantity";
    private static final String COLUMN_ITEM_PRICE = "item_price";
    private static final String COLUMN_USER_ID = "user_id" ;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createUserTableQuery = "CREATE TABLE " + TABLE_USERS +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        sqLiteDatabase.execSQL(createUserTableQuery);

        String createItemTableQuery = "CREATE TABLE " + TABLE_ITEMS +
                "(" + COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ITEM_NAME + " TEXT, " +
                COLUMN_ITEM_QUANTITY + " INTEGER, " +
                COLUMN_ITEM_PRICE + " REAL, " +
                COLUMN_USER_ID + " INTEGER, " + // Foreign key column
                "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        sqLiteDatabase.execSQL(createItemTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    // Add a new user to the database
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        long newRowId = db.insert(TABLE_USERS, null, values);
        db.close();
        return newRowId;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID, COLUMN_USERNAME, COLUMN_PASSWORD};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
        User user = null;

        int columnIndexId = cursor.getColumnIndex(COLUMN_ID);
        int columnIndexUsername = cursor.getColumnIndex(COLUMN_USERNAME);
        int columnIndexPassword = cursor.getColumnIndex(COLUMN_PASSWORD);

        if (cursor.moveToFirst()) {
            if (columnIndexId != -1 && columnIndexPassword != -1) {
                int id = cursor.getInt(columnIndexId);
                String password = cursor.getString(columnIndexPassword);
                user = new User(id, username, password);
            } else {
                if (columnIndexId == -1) {
                    // Custom error message for missing ID column
                    Log.e("Error", "Column " + COLUMN_ID + " not found in cursor");
                }
                if (columnIndexPassword == -1) {
                    // Custom error message for missing password column
                    Log.e("Error", "Column " + COLUMN_PASSWORD + " not found in cursor");
                }
            }
        }
        cursor.close();
        db.close();
        return user;
    }

    public long addItem(String itemName, int quantity, double price, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_NAME, itemName);
        values.put(COLUMN_ITEM_QUANTITY, quantity);
        values.put(COLUMN_ITEM_PRICE, price); // Store the price as a float value
        values.put(COLUMN_USER_ID, userId);   // Add the user_id
        long newRowId = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return newRowId;
    }


    public ArrayList<Item> getAllItems(int userId) {
        ArrayList<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ITEM_ID, COLUMN_ITEM_NAME, COLUMN_ITEM_QUANTITY, COLUMN_ITEM_PRICE};
        String selection = COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_ITEMS, projection, selection, selectionArgs, null, null, null);

        int columnIndexItemId = cursor.getColumnIndex(COLUMN_ITEM_ID);
        int columnIndexItemName = cursor.getColumnIndex(COLUMN_ITEM_NAME);
        int columnIndexQuantity = cursor.getColumnIndex(COLUMN_ITEM_QUANTITY);
        int columnIndexPrice = cursor.getColumnIndex(COLUMN_ITEM_PRICE);

        while (cursor.moveToNext()) {
            int itemId = -1;
            if (columnIndexItemId != -1) {
                itemId = cursor.getInt(columnIndexItemId);
            }

            String itemName = "";
            if (columnIndexItemName != -1) {
                itemName = cursor.getString(columnIndexItemName);
            }

            int quantity = -1;
            if (columnIndexQuantity != -1) {
                quantity = cursor.getInt(columnIndexQuantity);
            }

            double price = -1.0;
            if (columnIndexPrice != -1) {
                price = cursor.getDouble(columnIndexPrice);
            }

            Item item = new Item(itemId, itemName, quantity, price);
            itemList.add(item);
        }

        cursor.close();
        db.close();
        return itemList;
    }



    public void deleteItem(Item itemToDelete) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(itemToDelete.getItemId())});
        db.close();
    }

}
