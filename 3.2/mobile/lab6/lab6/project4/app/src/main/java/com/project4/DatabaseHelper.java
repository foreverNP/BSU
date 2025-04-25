package com.project4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "petshop_db";

    private static final String TABLE_FEEDS = "feeds";
    private static final String TABLE_MANUFACTURERS = "manufacturers";

    private static final String KEY_ID = "id";

    private static final String KEY_FEED_NAME = "name";
    private static final String KEY_FEED_PRICE = "price";
    private static final String KEY_FEED_MANUFACTURER_ID = "manufacturer_id";
    private static final String KEY_FEED_QUANTITY = "quantity";
    private static final String KEY_FEED_TYPE = "type";
    private static final String KEY_FEED_DESCRIPTION = "description";

    private static final String KEY_MANUFACTURER_NAME = "name";
    private static final String KEY_MANUFACTURER_COUNTRY = "country";
    private static final String KEY_MANUFACTURER_CONTACT_INFO = "contact_info";

    private static final String CREATE_TABLE_FEEDS = "CREATE TABLE " + TABLE_FEEDS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_FEED_NAME + " TEXT,"
            + KEY_FEED_PRICE + " REAL,"
            + KEY_FEED_MANUFACTURER_ID + " INTEGER,"
            + KEY_FEED_QUANTITY + " INTEGER,"
            + KEY_FEED_TYPE + " TEXT,"
            + KEY_FEED_DESCRIPTION + " TEXT,"
            + "FOREIGN KEY(" + KEY_FEED_MANUFACTURER_ID + ") REFERENCES " + TABLE_MANUFACTURERS + "(" + KEY_ID + ")"
            + ")";

    private static final String CREATE_TABLE_MANUFACTURERS = "CREATE TABLE " + TABLE_MANUFACTURERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_MANUFACTURER_NAME + " TEXT,"
            + KEY_MANUFACTURER_COUNTRY + " TEXT,"
            + KEY_MANUFACTURER_CONTACT_INFO + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MANUFACTURERS);
        db.execSQL(CREATE_TABLE_FEEDS);

        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MANUFACTURERS);

        onCreate(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        ContentValues manufacturer1 = new ContentValues();
        manufacturer1.put(KEY_MANUFACTURER_NAME, "Royal Canin");
        manufacturer1.put(KEY_MANUFACTURER_COUNTRY, "Франция");
        manufacturer1.put(KEY_MANUFACTURER_CONTACT_INFO, "info@royalcanin.com");
        long manufacturer1Id = db.insert(TABLE_MANUFACTURERS, null, manufacturer1);

        ContentValues manufacturer2 = new ContentValues();
        manufacturer2.put(KEY_MANUFACTURER_NAME, "Purina");
        manufacturer2.put(KEY_MANUFACTURER_COUNTRY, "США");
        manufacturer2.put(KEY_MANUFACTURER_CONTACT_INFO, "contact@purina.com");
        long manufacturer2Id = db.insert(TABLE_MANUFACTURERS, null, manufacturer2);

        ContentValues manufacturer3 = new ContentValues();
        manufacturer3.put(KEY_MANUFACTURER_NAME, "Hill's");
        manufacturer3.put(KEY_MANUFACTURER_COUNTRY, "США");
        manufacturer3.put(KEY_MANUFACTURER_CONTACT_INFO, "care@hills.com");
        long manufacturer3Id = db.insert(TABLE_MANUFACTURERS, null, manufacturer3);

        ContentValues manufacturer4 = new ContentValues();
        manufacturer4.put(KEY_MANUFACTURER_NAME, "Acana");
        manufacturer4.put(KEY_MANUFACTURER_COUNTRY, "Канада");
        manufacturer4.put(KEY_MANUFACTURER_CONTACT_INFO, "support@acana.com");
        long manufacturer4Id = db.insert(TABLE_MANUFACTURERS, null, manufacturer4);

        // Добавление кормов
        ContentValues feed1 = new ContentValues();
        feed1.put(KEY_FEED_NAME, "Royal Canin Maxi Adult");
        feed1.put(KEY_FEED_PRICE, 3500.0);
        feed1.put(KEY_FEED_MANUFACTURER_ID, manufacturer1Id);
        feed1.put(KEY_FEED_QUANTITY, 150);
        feed1.put(KEY_FEED_TYPE, "Сухой корм для собак");
        feed1.put(KEY_FEED_DESCRIPTION, "Для крупных пород от 15 месяцев");
        db.insert(TABLE_FEEDS, null, feed1);

        ContentValues feed2 = new ContentValues();
        feed2.put(KEY_FEED_NAME, "Royal Canin Kitten");
        feed2.put(KEY_FEED_PRICE, 1800.0);
        feed2.put(KEY_FEED_MANUFACTURER_ID, manufacturer1Id);
        feed2.put(KEY_FEED_QUANTITY, 200);
        feed2.put(KEY_FEED_TYPE, "Сухой корм для котят");
        feed2.put(KEY_FEED_DESCRIPTION, "Для котят до 12 месяцев");
        db.insert(TABLE_FEEDS, null, feed2);

        ContentValues feed3 = new ContentValues();
        feed3.put(KEY_FEED_NAME, "Purina Pro Plan Adult");
        feed3.put(KEY_FEED_PRICE, 2800.0);
        feed3.put(KEY_FEED_MANUFACTURER_ID, manufacturer2Id);
        feed3.put(KEY_FEED_QUANTITY, 180);
        feed3.put(KEY_FEED_TYPE, "Сухой корм для собак");
        feed3.put(KEY_FEED_DESCRIPTION, "Для взрослых собак всех пород");
        db.insert(TABLE_FEEDS, null, feed3);

        ContentValues feed4 = new ContentValues();
        feed4.put(KEY_FEED_NAME, "Purina ONE Indoor");
        feed4.put(KEY_FEED_PRICE, 1500.0);
        feed4.put(KEY_FEED_MANUFACTURER_ID, manufacturer2Id);
        feed4.put(KEY_FEED_QUANTITY, 250);
        feed4.put(KEY_FEED_TYPE, "Сухой корм для кошек");
        feed4.put(KEY_FEED_DESCRIPTION, "Для домашних кошек");
        db.insert(TABLE_FEEDS, null, feed4);

        ContentValues feed5 = new ContentValues();
        feed5.put(KEY_FEED_NAME, "Hill's Science Plan");
        feed5.put(KEY_FEED_PRICE, 3200.0);
        feed5.put(KEY_FEED_MANUFACTURER_ID, manufacturer3Id);
        feed5.put(KEY_FEED_QUANTITY, 120);
        feed5.put(KEY_FEED_TYPE, "Сухой корм для собак");
        feed5.put(KEY_FEED_DESCRIPTION, "С курицей для взрослых собак");
        db.insert(TABLE_FEEDS, null, feed5);

        ContentValues feed6 = new ContentValues();
        feed6.put(KEY_FEED_NAME, "Acana Wild Prairie");
        feed6.put(KEY_FEED_PRICE, 4500.0);
        feed6.put(KEY_FEED_MANUFACTURER_ID, manufacturer4Id);
        feed6.put(KEY_FEED_QUANTITY, 90);
        feed6.put(KEY_FEED_TYPE, "Сухой корм для кошек");
        feed6.put(KEY_FEED_DESCRIPTION, "Беззерновой корм с птицей и рыбой");
        db.insert(TABLE_FEEDS, null, feed6);

        ContentValues feed7 = new ContentValues();
        feed7.put(KEY_FEED_NAME, "Acana Pacifica Dog");
        feed7.put(KEY_FEED_PRICE, 5100.0);
        feed7.put(KEY_FEED_MANUFACTURER_ID, manufacturer4Id);
        feed7.put(KEY_FEED_QUANTITY, 80);
        feed7.put(KEY_FEED_TYPE, "Сухой корм для собак");
        feed7.put(KEY_FEED_DESCRIPTION, "Беззерновой корм с рыбой");
        db.insert(TABLE_FEEDS, null, feed7);

        ContentValues feed8 = new ContentValues();
        feed8.put(KEY_FEED_NAME, "Hill's Prescription Diet");
        feed8.put(KEY_FEED_PRICE, 3800.0);
        feed8.put(KEY_FEED_MANUFACTURER_ID, manufacturer3Id);
        feed8.put(KEY_FEED_QUANTITY, 100);
        feed8.put(KEY_FEED_TYPE, "Диетический корм");
        feed8.put(KEY_FEED_DESCRIPTION, "Для кошек с проблемами ЖКТ");
        db.insert(TABLE_FEEDS, null, feed8);
    }

    public List<Feed> getAllFeeds() {
        List<Feed> feedList = new ArrayList<>();
        String selectQuery = "SELECT f.*, m.name as manufacturer_name FROM " + TABLE_FEEDS + " f "
                + "JOIN " + TABLE_MANUFACTURERS + " m ON f.manufacturer_id = m.id";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Feed feed = new Feed(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_FEED_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_MANUFACTURER_ID)),
                        cursor.getString(cursor.getColumnIndex("manufacturer_name")),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_DESCRIPTION))
                );
                feedList.add(feed);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return feedList;
    }

    public List<Feed> getSortedFeeds(String sortBy, boolean ascending) {
        List<Feed> feedList = new ArrayList<>();
        String orderDirection = ascending ? "ASC" : "DESC";
        String selectQuery = "SELECT f.*, m.name as manufacturer_name FROM " + TABLE_FEEDS + " f "
                + "JOIN " + TABLE_MANUFACTURERS + " m ON f.manufacturer_id = m.id "
                + "ORDER BY f." + sortBy + " " + orderDirection;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Feed feed = new Feed(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_FEED_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_MANUFACTURER_ID)),
                        cursor.getString(cursor.getColumnIndex("manufacturer_name")),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_DESCRIPTION))
                );
                feedList.add(feed);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return feedList;
    }

    public List<Feed> getFeedsAbovePrice(double minPrice) {
        List<Feed> feedList = new ArrayList<>();
        String selectQuery = "SELECT f.*, m.name as manufacturer_name FROM " + TABLE_FEEDS + " f "
                + "JOIN " + TABLE_MANUFACTURERS + " m ON f.manufacturer_id = m.id "
                + "WHERE f.price > " + minPrice;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Feed feed = new Feed(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_FEED_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_MANUFACTURER_ID)),
                        cursor.getString(cursor.getColumnIndex("manufacturer_name")),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_DESCRIPTION))
                );
                feedList.add(feed);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return feedList;
    }

    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> manufacturerList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MANUFACTURERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Manufacturer manufacturer = new Manufacturer(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_MANUFACTURER_NAME)),
                        cursor.getString(cursor.getColumnIndex(KEY_MANUFACTURER_COUNTRY)),
                        cursor.getString(cursor.getColumnIndex(KEY_MANUFACTURER_CONTACT_INFO))
                );
                manufacturerList.add(manufacturer);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return manufacturerList;
    }

    public List<Feed> getFeedsGroupedByManufacturer(int manufacturerId) {
        List<Feed> feedList = new ArrayList<>();
        String selectQuery = "SELECT f.*, m.name as manufacturer_name FROM " + TABLE_FEEDS + " f "
                + "JOIN " + TABLE_MANUFACTURERS + " m ON f.manufacturer_id = m.id "
                + "WHERE f.manufacturer_id = " + manufacturerId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Feed feed = new Feed(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_NAME)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_FEED_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_MANUFACTURER_ID)),
                        cursor.getString(cursor.getColumnIndex("manufacturer_name")),
                        cursor.getInt(cursor.getColumnIndex(KEY_FEED_QUANTITY)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_TYPE)),
                        cursor.getString(cursor.getColumnIndex(KEY_FEED_DESCRIPTION))
                );
                feedList.add(feed);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return feedList;
    }

    public double getMaxPriceByManufacturer(int manufacturerId) {
        double maxPrice = 0;
        String selectQuery = "SELECT MAX(price) as max_price FROM " + TABLE_FEEDS +
                " WHERE manufacturer_id = " + manufacturerId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            maxPrice = cursor.getDouble(cursor.getColumnIndex("max_price"));
        }

        cursor.close();
        return maxPrice;
    }

    public double getMinPriceByManufacturer(int manufacturerId) {
        double minPrice = 0;
        String selectQuery = "SELECT MIN(price) as min_price FROM " + TABLE_FEEDS +
                " WHERE manufacturer_id = " + manufacturerId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            minPrice = cursor.getDouble(cursor.getColumnIndex("min_price"));
        }

        cursor.close();
        return minPrice;
    }

    public double getAvgPriceByManufacturer(int manufacturerId) {
        double avgPrice = 0;
        String selectQuery = "SELECT AVG(price) as avg_price FROM " + TABLE_FEEDS +
                " WHERE manufacturer_id = " + manufacturerId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            avgPrice = cursor.getDouble(cursor.getColumnIndex("avg_price"));
        }

        cursor.close();
        return avgPrice;
    }

    public int getTotalQuantityByManufacturer(int manufacturerId) {
        int totalQuantity = 0;
        String selectQuery = "SELECT SUM(quantity) as total_quantity FROM " + TABLE_FEEDS +
                " WHERE manufacturer_id = " + manufacturerId;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            totalQuantity = cursor.getInt(cursor.getColumnIndex("total_quantity"));
        }

        cursor.close();
        return totalQuantity;
    }

    public int getTotalFeedsCount() {
        int count = 0;
        String selectQuery = "SELECT COUNT(*) as total_count FROM " + TABLE_FEEDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("total_count"));
        }

        cursor.close();
        return count;
    }
}