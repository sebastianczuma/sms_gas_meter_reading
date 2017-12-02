package com.scz.odczytgazomierza.Database;

/**
 * Created by sebastianczuma on 18.08.2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.scz.odczytgazomierza.RecyclerView2.Item2;

import java.util.LinkedList;
import java.util.List;

public class DbHandler2 extends SQLiteOpenHelper {

    public DbHandler2(Context context) {
        super(context, "main_database_2.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table main_table_2(" +
                        "ID integer primary key autoincrement," +
                        "BANK_ACCOUNT_NUMBER text," +
                        "NAME text);" +
                        "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addItem2(Item2 item2) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("BANK_ACCOUNT_NUMBER", item2.getBankAccountNumber());
        values.put("NAME", item2.getName());
        db.insertOrThrow("main_table_2", null, values);
    }

    public boolean searchIfDbContains2(String bankAccountNumber) {
        String[] columns = {"BANK_ACCOUNT_NUMBER"};
        String[] selectionArgs = {bankAccountNumber};

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("main_table_2", columns, "BANK_ACCOUNT_NUMBER=?", selectionArgs, null, null, null, "1");
        boolean answer = cursor.getCount() > 0;
        cursor.close();

        return answer;
    }

    public void updateOneItem2(String oldBankAccountNumber, Item2 item2) {
        ContentValues values = new ContentValues();
        values.put("BANK_ACCOUNT_NUMBER", item2.getBankAccountNumber());
        values.put("NAME", item2.getName());

        SQLiteDatabase db = getReadableDatabase();

        String[] args = new String[]{oldBankAccountNumber};
        db.update("main_table_2", values, "BANK_ACCOUNT_NUMBER=?", args);
    }

    public void deleteOneItem2(String bankAccountNumber) {
        SQLiteDatabase db = getWritableDatabase();
        String[] args = {
                bankAccountNumber
        };
        db.delete("main_table_2", "BANK_ACCOUNT_NUMBER=?", args);
    }

    public List<Item2> returnAll2() {
        List<Item2> items2 = new LinkedList<>();
        String[] columns = {"ID", "BANK_ACCOUNT_NUMBER", "NAME"};
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("main_table_2", columns, null, null, null, null, "ID DESC");
        while (cursor.moveToNext()) {
            Item2 item2 = new Item2();
            item2.setId(cursor.getLong(0));
            item2.setBankAccountNumber(cursor.getString(1));
            item2.setName(cursor.getString(2));
            items2.add(item2);
        }
        cursor.close();
        return items2;
    }

    public String searchName(String bankAccountNumber) {
        String[] columns = {"NAME"};
        String[] selectionArgs = {bankAccountNumber};
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query("main_table_2", columns, "BANK_ACCOUNT_NUMBER=?", selectionArgs, null, null, null, "1");
        String name = "";
        while (cursor.moveToNext()) {
            name = cursor.getString(0);
        }

        cursor.close();

        return name;
    }
}
