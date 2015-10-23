package com.example.brandon.jpbestbuy;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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


	public static String getAddressFromLocation(Context mContext, double latitude, double longitude){
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(mContext, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
			String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();
			Log.d(TAG, "getAddressFromLocation:" +addresses.get(0).getAddressLine(0).toString());
			return addresses.get(0).getAddressLine(0).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


    public static Location getGPSLocation(LocationManager locationManager){
        String bestProvider = LocationManager.GPS_PROVIDER;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            Criteria criteria = new Criteria();	//資訊提供者選取標準
            bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            return location;
        }
        return null;
    }


}
