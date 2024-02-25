package com.example.project3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phoneOwners.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "phone_owner";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_MIDDLE_NAME = "middle_name";
    public static final String COLUMN_BIRTH_DATE = "birth_date";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";

    // Новые поля:
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_YEAR_CONCLUSION = "year_conclusion";
    public static final String COLUMN_TARIFF_NAME = "tariff_name";
    public static final String COLUMN_TARIFF_COST = "tariff_cost";

    public DBRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_MIDDLE_NAME + " TEXT, " +
                COLUMN_BIRTH_DATE + " TEXT, " +
                COLUMN_PHONE_NUMBER + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_YEAR_CONCLUSION + " INTEGER, " +
                COLUMN_TARIFF_NAME + " TEXT, " +
                COLUMN_TARIFF_COST + " TEXT);";
        db.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2) {
            // 1. Переименуем старую таблицу во временную
            db.execSQL("ALTER TABLE " + TABLE_NAME + " RENAME TO " + TABLE_NAME + "_old");

            // 2. Создадим новую таблицу с обновлённой схемой
            String createNewTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LAST_NAME + " TEXT, " +
                    COLUMN_FIRST_NAME + " TEXT, " +
                    COLUMN_MIDDLE_NAME + " TEXT, " +
                    COLUMN_BIRTH_DATE + " TEXT, " +
                    COLUMN_PHONE_NUMBER + " TEXT, " +
                    COLUMN_ADDRESS + " TEXT, " +
                    COLUMN_YEAR_CONCLUSION + " INTEGER, " +
                    COLUMN_TARIFF_NAME + " TEXT, " +
                    COLUMN_TARIFF_COST + " TEXT);";
            db.execSQL(createNewTableSQL);

            // 3. Перенесём данные из старой таблицы в новую, задавая значения по умолчанию для новых полей
            String copyDataSQL = "INSERT INTO " + TABLE_NAME + " (" +
                    COLUMN_ID + ", " +
                    COLUMN_LAST_NAME + ", " +
                    COLUMN_FIRST_NAME + ", " +
                    COLUMN_MIDDLE_NAME + ", " +
                    COLUMN_BIRTH_DATE + ", " +
                    COLUMN_PHONE_NUMBER + ", " +
                    COLUMN_ADDRESS + ", " +
                    COLUMN_YEAR_CONCLUSION + ", " +
                    COLUMN_TARIFF_NAME + ", " +
                    COLUMN_TARIFF_COST +
                    ") SELECT " +
                    COLUMN_ID + ", " +
                    COLUMN_LAST_NAME + ", " +
                    COLUMN_FIRST_NAME + ", " +
                    COLUMN_MIDDLE_NAME + ", " +
                    COLUMN_BIRTH_DATE + ", " +
                    COLUMN_PHONE_NUMBER + ", " +
                    "'', 0, '', '' FROM " + TABLE_NAME + "_old";
            db.execSQL(copyDataSQL);

            // 4. Удаляем временную таблицу
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + "_old");
        }
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
            values.put(COLUMN_ADDRESS, "Адрес" + i);
            values.put(COLUMN_YEAR_CONCLUSION, 2000 + i);
            values.put(COLUMN_TARIFF_NAME, "Тариф" + i);
            values.put(COLUMN_TARIFF_COST, "100" + i);
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public void addRecord(String lastName, String firstName, String middleName, String birthDate, String phoneNumber,
                          String address, int yearConclusion, String tariffName, String tariffCost) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_MIDDLE_NAME, middleName);
        values.put(COLUMN_BIRTH_DATE, birthDate);
        values.put(COLUMN_PHONE_NUMBER, phoneNumber);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_YEAR_CONCLUSION, yearConclusion);
        values.put(COLUMN_TARIFF_NAME, tariffName);
        values.put(COLUMN_TARIFF_COST, tariffCost);
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
