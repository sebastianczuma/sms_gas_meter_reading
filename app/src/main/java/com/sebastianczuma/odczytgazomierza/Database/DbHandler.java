package com.sebastianczuma.odczytgazomierza.Database;

/**
 * Created by sebastianczuma on 18.08.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sebastianczuma.odczytgazomierza.RecyclerView.Item;

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
        ContentValues wartosci = new ContentValues();
        wartosci.put("DATE", item.getDate());
        wartosci.put("METER_READING", item.getMeterReading());
        wartosci.put("BANK_ACCOUNT_NUMBER", item.getBankAccountNumber());
        wartosci.put("PHONE_NUMBER", item.getPhoneNumber());
        db.insertOrThrow("main_table", null, wartosci);
    }

    public String deleteOneItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String number = Integer.toString(id);
        String[] argumenty = {
                number
        };
        db.delete("main_table", "ID=?", argumenty);
        return number;
    }

    public List<Item> returnAll() {
        List<Item> floors = new LinkedList<>();
        String[] columns = {"ID", "DATE", "METER_READING", "BANK_ACCOUNT_NUMBER", "PHONE_NUMBER"};
        SQLiteDatabase db = getReadableDatabase();

        Cursor kursor = db.query("main_table", columns, null, null, null, null, "ID DESC");
        while (kursor.moveToNext()) {
            Item item = new Item();
            item.setId(kursor.getLong(0));
            item.setDate(kursor.getString(1));
            item.setMeterReading(kursor.getString(2));
            item.setBankAccountNumber(kursor.getString(3));
            item.setPhoneNumber(kursor.getString(4));
            floors.add(item);
        }
        kursor.close();
        return floors;
    }
}
