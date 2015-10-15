package com.example.brandon.jpbestbuy;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Brandon on 15/6/28.
 */
public class StoreListAdapter extends CursorAdapter {

    final String TAG = Utils.TAG;
    final Context context;
    final Cursor cursor;

    public StoreListAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
        this.cursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView fline = (TextView) view.findViewById(R.id.firstLine);
        TextView sline = (TextView) view.findViewById(R.id.secondLine);
        // Extract properties from cursor
        String store_name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        int threshold = cursor.getInt(cursor.getColumnIndexOrThrow("THRESHOLD"));
        int discount = cursor.getInt(cursor.getColumnIndexOrThrow("DISCOUNT"));
        // Populate fields with extracted properties
        String slineText = "above $" + threshold + " JPD can get " + discount + "% discount";
        fline.setText(store_name);
        sline.setText(String.valueOf(slineText));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.store_list_item_adapter, parent, false);
    }

}
