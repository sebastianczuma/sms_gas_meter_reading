package com.scz.odczytgazomierza.Database;

/**
 * Created by sebastianczuma on 18.08.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.scz.odczytgazomierza.RecyclerView.Item;

import java.util.LinkedList;
import java.util.List;

public class DbHandler extends SQLiteOpenHelper {

    public DbHandler(Context context) {
        super(context, "main_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table main_table(" +
                        "ID integer primary key autoincrement," +
                        "DATE text," +
                        "METER_READING text," +
                        "BANK_ACCOUNT_NUMBER text," +
                        "PHONE_NUMBER text);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Urzadzenia
    public void addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DATE", item.getDate());
        values.put("METER_READING", item.getMeterReading());
        values.put("BANK_ACCOUNT_NUMBER", item.getBankAccountNumber());
        values.put("PHONE_NUMBER", item.getPhoneNumber());
        db.insertOrThrow("main_table", null, values);
    }

    public String deleteOneItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String number = Integer.toString(id);
        String[] args = {
                number
        };
        db.delete("main_table", "ID=?", args);
        return number;
    }

    public List<Item> returnAll() {
        List<Item> items = new LinkedList<>();
        String[] columns = {"ID", "DATE", "METER_READING", "BANK_ACCOUNT_NUMBER", "PHONE_NUMBER"};
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("main_table", columns, null, null, null, null, "ID DESC");
        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getLong(0));
            item.setDate(cursor.getString(1));
            item.setMeterReading(cursor.getString(2));
            item.setBankAccountNumber(cursor.getString(3));
            item.setPhoneNumber(cursor.getString(4));
            items.add(item);
        }
        cursor.close();
        return items;
    }
}
