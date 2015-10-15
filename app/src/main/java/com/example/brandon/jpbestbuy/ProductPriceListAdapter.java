package com.example.brandon.jpbestbuy;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Brandon on 15/7/10.
 */
public class ProductPriceListAdapter extends CursorAdapter {
    final String TAG = Utils.TAG + "(PPLAdapter)";

    private HashMap<Integer, ArrayList<String>> productName = new HashMap<Integer, ArrayList<String>>();

    public ProductPriceListAdapter(Context context, Cursor c) {
        super(context, c, 0);
        getProductName();

    }

    private void getProductName(){
        Cursor cursor = DB.getAllProduct();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            ArrayList<String> pInfo = new ArrayList<String>();
            pInfo.add(cursor.getString(1));
            pInfo.add(cursor.getString(2));
            productName.put(cursor.getInt(0), pInfo);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d(TAG, "@productName:" + productName);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_price_list_adapter, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView spName = (TextView) view.findViewById(R.id.tv_store_product_name);
        TextView spPrice = (TextView) view.findViewById(R.id.tv_store_product_price);
        // Extract properties from cursor
        Integer pid = cursor.getInt(cursor.getColumnIndexOrThrow("PID"));
        String product_price = cursor.getString(cursor.getColumnIndexOrThrow("PRICE"));
        String product_name = productName.get(pid).get(0);
        String product_amount = productName.get(pid).get(1);

        // Populate fields with extracted properties
        spName.setText(product_name + "(" + product_amount + ")");
        spPrice.setText(product_price);

    }
}
