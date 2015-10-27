package com.example.brandon.jpbestbuy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by ty on 2015/10/27.
 */
public class Mockup {

    private final static String TAG="Mockup";

    public static boolean noMockupInit(Context context) {
        /**
         * if key "inited" exist in sharePreference,
         * then return false.
         * else return true.
         */
        String key = "mockup_inited";

        SharedPreferences pref = context.getSharedPreferences("ShopPanda", Context.MODE_PRIVATE);
        return (!pref.getBoolean(key, false));

    }

    private static void setPreference(Context context, String key, Boolean value) {
        Log.d(TAG, "setPreference");
        SharedPreferences.Editor prefEditor = context.getSharedPreferences("ShopPanda", Context.MODE_PRIVATE).edit();
        prefEditor.putBoolean(key, value);
        prefEditor.commit();

    }


    public static void initMockupCase1(Context context) {
        setPreference(context, "mockup_inited", true);

        DB.addProduct("Prodcut1", 5);
        DB.addProduct("Prodcut2", 2);
        DB.addProduct("Prodcut3", 1);
        DB.addProduct("Prodcut4", 1);
        DB.addProduct("Prodcut5", 5);

        DB.addStore("OS drug", 5400, 8, "Null");
        DB.addPrice(1, 1, 200);
        DB.addPrice(1, 2, 200);
        DB.addPrice(1, 3, 3000);
        DB.addPrice(1, 4, 400);

        String addr = Utils.getAddressFromLocation(context, Double.valueOf("25.0497191"), Double.valueOf("121.5747108"));
        DB.addStore("Sun drug", 5400, 8, "25.0497191,121.5747108;"+addr);
        DB.addPrice(2, 1, 150);
        DB.addPrice(2, 2, 250);
        DB.addPrice(2, 3, 3000);
        DB.addPrice(2, 5, 550);
        DB.addPrice(2, 4, 2400);

    }


    public static void cleanMockupCase(Context context){
        Log.d(TAG,"clean SQL and SharedPreferences data of MockupCase");
        SharedPreferences.Editor prefEditor = context.getSharedPreferences("ShopPanda", Context.MODE_PRIVATE).edit();
        prefEditor.clear();
        prefEditor.commit();
    }

}
