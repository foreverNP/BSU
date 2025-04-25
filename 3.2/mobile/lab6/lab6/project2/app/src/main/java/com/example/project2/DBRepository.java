package com.example.project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phoneOwners.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "phone_owner";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_MIDDLE_NAME = "middle_name";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";

    public DBRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSQL = "CREATE TABLE if not exists " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_MIDDLE_NAME + " TEXT, " +
                COLUMN_BIRTH_DATE + " TEXT, " +
                COLUMN_PHONE_NUMBER + " TEXT);";
        db.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void initData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);

        for (int i = 1; i <= 5; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LAST_NAME, "Фамилия" + i);
            values.put(COLUMN_FIRST_NAME, "Имя" + i);
            values.put(COLUMN_MIDDLE_NAME, "Отчество" + i);
            values.put(COLUMN_BIRTH_DATE, "1990-01-0" + i);
            values.put(COLUMN_PHONE_NUMBER, "+7-900-0000" + i);
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public void addRecord(String lastName, String firstName, String middleName, String birthDate, String phoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_MIDDLE_NAME, middleName);
        values.put(COLUMN_BIRTH_DATE, birthDate);
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateRecord(int id, String newPhoneNumber) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE_NUMBER, newPhoneNumber);
        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = { String.valueOf(id) };
        db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
    }

    public Cursor getAllRecords() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }
}
