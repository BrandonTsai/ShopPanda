package com.example.brandon.jpbestbuy;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by ty on 2015/7/9.
 */
public class Utils {

	public final static String TAG="ShopPanda";

	public Utils(){
		Log.d(TAG, "Utils Obj created!");
	}

	public static JSONArray cur2Json(Cursor cursor) {

		JSONArray resultSet = new JSONArray();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			int totalColumn = cursor.getColumnCount();
			JSONObject rowObject = new JSONObject();
			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {
					try {
						rowObject.put(cursor.getColumnName(i),
								cursor.getString(i));
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
			}
			resultSet.put(rowObject);
			cursor.moveToNext();
		}
		cursor.moveToFirst();
		return resultSet;

	}

	public static JSONObject hashmap2Json(HashMap<String, String> hmap) {

		JSONObject resultSet = new JSONObject();
		try {
			for (String key : hmap.keySet() ){
				resultSet.put(key, hmap.get(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultSet;

	}

	public static ArrayList<HashMap<String, String>> cur2ArrayList(Cursor cursor) {
		ArrayList<HashMap<String,String>> resultSet = new ArrayList<HashMap<String, String>>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			int totalColumn = cursor.getColumnCount();
            HashMap<String,String> row = new HashMap<>();
			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {
					try {
						row.put(cursor.getColumnName(i),
								cursor.getString(i));
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
			}
			resultSet.add(row);
			cursor.moveToNext();
		}
		cursor.moveToFirst();
        Log.d(TAG, "cur2ArrayList:" + resultSet.toString());
		return resultSet;
	}

	public static HashMap<Integer, HashMap<String, String>> cur2HashMap(Cursor cursor) {
		HashMap<Integer, HashMap<String, String>> resultSet = new HashMap<Integer, HashMap<String, String>>();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			int totalColumn = cursor.getColumnCount();
			HashMap<String,String> row = new HashMap<>();
			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {
					try {
						row.put(cursor.getColumnName(i),
								cursor.getString(i));
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
			}
			resultSet.put(cursor.getInt(0), row);
			cursor.moveToNext();
		}
		cursor.moveToFirst();
		Log.d(TAG, "cur2HashMap:" + resultSet.toString());
		return resultSet;
	}


	public static String strArrayList2String(ArrayList<String> list){

		String result = "";

		for (String s: list){
			if (result.equals("")){
				result = s;
			} else {
				result += "," + s;
			}
		}

		return result;
	}


	public static String treeSet2String(TreeSet<Integer> set){

		String result = "[";

		for(Integer s : set) {
			if (result.equals("[")) {
				result += String.valueOf(s);
			} else {
				result += "," + String.valueOf(s);
			}
		}

		result += "]";
		return result;

	}
}
