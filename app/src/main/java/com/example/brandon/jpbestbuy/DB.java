package com.example.brandon.jpbestbuy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by ty on 2015/7/8.
 */
public class DB{

    static SQLiteDatabase db;
    final static String DB_NAME = "JPbestbuy";
    final static String TAG = Utils.TAG + "(DataBase)";

    public DB(Context context){
        initDatabase(context);
    }

    public static void initDatabase(Context context){
        File database=context.getApplicationContext().getDatabasePath(DB_NAME+".db");
        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            db = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
            createTable();
            Log.d(TAG, "Init Database");
        } else {
            Log.d(TAG, "Found Database " + DB_NAME);
        }
    }

    private static void createTable() {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS stores ("
                    + "_id INTEGER PRIMARY KEY autoincrement,"
                    + "NAME TEXT,"
                    + "THRESHOLD INTEGER,"
                    + "DISCOUNT INTEGER,"
                    + "LOCATION TEXT"
                    + ");");
            db.execSQL("CREATE TABLE IF NOT EXISTS products ("
                    + "_id INTEGER PRIMARY KEY autoincrement,"
                    + "NAME TEXT,"
                    + "AMOUNT INTEGER"
                    + ");");

            db.execSQL("CREATE TABLE IF NOT EXISTS prices ("
                    + "_id INTEGER PRIMARY KEY autoincrement,"
                    + "SID INTEGER,"
                    + "PID INTEGER,"
                    + "PRICE INTEGER"
                    + ");");

        } catch (Exception e) {
            Log.d(TAG, "create table error");
            e.printStackTrace();
        }
    }


    /*
    Store Table
     */
    public static void addStore(String name, int threshold, int discount, String location) {
        ContentValues cv=new ContentValues(4);

        cv.put("NAME", name);
        cv.put("THRESHOLD", threshold);
        cv.put("DISCOUNT", discount);
        cv.put("LOCATION", location);

        db.insert("stores", null, cv);

    }

    public static void updateStore(int id, String name, int threshold, int discount) {
        ContentValues cv=new ContentValues(3);

        cv.put("NAME", name);
        cv.put("THRESHOLD", threshold);
        cv.put("DISCOUNT", discount);

        db.update("stores", cv, "_id=" + id, null);

    }

    public static void delStore(int id) {
        db.delete("stores", "_id=" + id, null);
        db.delete("prices", "SID=" + id, null);
    }

    public static Cursor getStore() {
        Cursor cursor = db.query("stores", new String[]{"_id", "NAME", "THRESHOLD", "DISCOUNT", "LOCATION"}, null,
                null, null, null, null);

        return cursor;
    }


    public static ArrayList<HashMap<String, String>> getStoreArrayList(){
        Cursor cursor = getStore();
        return Utils.cur2ArrayList(cursor);
    }


    /*
    Product Table
     */
    public static Cursor getAllProduct() {
        Cursor cursor = db.query("products", new String[]{"_id", "NAME", "AMOUNT"}, null,
                null, null, null, null);

        return cursor;
    }


    public static HashMap<String,String> getProduct(Integer pid) {
        Cursor cursor = db.query("products", new String[] { "_id", "NAME", "AMOUNT" }, "_id="+pid,
                null, null, null, null);
        ArrayList<HashMap<String,String>> pInfo = Utils.cur2ArrayList(cursor);
        Log.d(TAG, "get product:" + pInfo.toString());
        return pInfo.get(0);
    }

    public static void addProduct(String name, int amount) {
        ContentValues cv=new ContentValues(2);

        cv.put("NAME", name);
        cv.put("AMOUNT", amount);

        db.insert("products", null, cv);
    }


    /*
    Price Table
     */
    public static Cursor getAllPrice(Integer sid) {
        Cursor cursor = db.query("prices", new String[] { "_id", "SID", "PID", "PRICE" },
                "SID="+sid , null, null, null, null);

        return cursor;
    }

    public static Cursor getPriceOrderByPID() {
        String orderby = "PID, PRICE";
        Cursor cursor = db.query("prices", new String[] { "_id", "SID", "PID", "PRICE" },
                null , null, null, null, orderby);
        return cursor;
    }

    public static HashMap<Integer,Integer> getStorePriceByPID(Integer pid) {
        String orderby = "PRICE";
        Cursor cursor = db.query("prices", new String[] { "_id", "SID", "PID", "PRICE" },
                "PID="+pid , null, null, null, orderby);
        HashMap<Integer,Integer> result = new HashMap<>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            result.put(cursor.getInt(1), cursor.getInt(3));
            cursor.moveToNext();
        }
        cursor.moveToFirst();
        return result;
    }

    public static Integer getPrice(Integer sid, Integer pid){
        String selection = "SID=" + sid + " AND PID=" + pid;
        Cursor cursor = db.query("prices", new String[] { "PRICE" }, selection,
                null, null, null, null);

        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }

        return -1;
    }

    public static void addPrice(Integer sid, Integer pid, Integer price){
        ContentValues cv = new ContentValues(3);
        cv.put("SID", sid);
        cv.put("PID", pid);
        cv.put("PRICE", price);

        Integer existPrice = getPrice(sid, pid);
        if (existPrice.equals(-1)) {
            db.insert("prices", null, cv);
            Log.d(TAG, "Add product price:" + sid + "," + pid + "," + price);
        } else {
            db.update("prices", cv, "sid=" + sid + " AND pid=" + pid, null);
            Log.d(TAG, "Update product price:" + sid + "," + pid + "," + price);
        }
    }

    public static void updatePrice(Integer sid, Integer pid, Integer price){
        ContentValues cv=new ContentValues(3);
        cv.put("SID", sid);
        cv.put("PID", pid);
        cv.put("PRICE", price);
        db.update("prices", cv, "SID=" + sid + " AND PID="+pid, null);
    }

    public static void deletePrice(Integer sid, Integer pid){
        db.delete("prices", "SID=" + sid + " AND PID=" + pid, null);
    }



    /*
    Combine
     */
    public static ArrayList<HashMap<String, Object>> getAllProuctWithPrice(Integer sid){
        String MY_QUERY = "SELECT a._id,PID,NAME,AMOUNT,PRICE FROM prices a INNER JOIN products b ON a.PID=b._id WHERE a.SID="+sid;
        //ArrayList<HashMap<String, String>> result = Utils.cur2ArrayList(db.rawQuery(MY_QUERY, null));
        Cursor cursor = db.rawQuery(MY_QUERY, null);
        ArrayList<HashMap<String,Object>> resultSet = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            HashMap<String,Object> row = new HashMap<>();
            row.put("_id", cursor.getInt(0));
            row.put("PID", cursor.getInt(1));
            row.put("NAME", cursor.getString(2));
            row.put("AMOUNT", cursor.getInt(3));
            row.put("PRICE", cursor.getInt(4));
            resultSet.add(row);
            cursor.moveToNext();
        }
        cursor.moveToFirst();
        return resultSet;

    }


}
