package com.example.brandon.jpbestbuy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ty on 2015/10/8.
 */
public class SelectStoreListAdapter extends BaseAdapter {

    private LayoutInflater myInflater;
    ArrayList<HashMap<String, String>> items;

    public SelectStoreListAdapter(Context c, ArrayList<HashMap<String, String>> stores){
        this.myInflater = LayoutInflater.from(c);
        this.items = stores;
    }



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
