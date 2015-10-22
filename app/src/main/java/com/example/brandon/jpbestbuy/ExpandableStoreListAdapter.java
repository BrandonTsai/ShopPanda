package com.example.brandon.jpbestbuy;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ty on 2015/10/22.
 */
public class ExpandableStoreListAdapter extends BaseExpandableListAdapter {

    private ArrayList<Store> shopList;
    private ArrayList<ArrayList<HashMap<String, Object>>> shopProductsList;

    private Context mContext;

    public ExpandableStoreListAdapter(Context context, HashMap<Integer, Store> shopList){
        this.mContext = context;
        this.shopList = new ArrayList<>();
        this.shopProductsList = new ArrayList<>();

        for (Store s: shopList.values()){
            this.shopList.add(s);
            ArrayList<HashMap<String, Object>> buyProductsInfo = s.getBuyProductsInfo();
            shopProductsList.add(buyProductsInfo);
        }
    }


    @Override
    public int getGroupCount() {
        return shopList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return shopProductsList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return shopList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return shopProductsList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Store s = shopList.get(groupPosition);
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.parseColor("#ccddff"));
        ll.setPadding(50, 10, 10, 10);

        LinearLayout mainLyaout = new LinearLayout(mContext);
        mainLyaout.setOrientation(LinearLayout.HORIZONTAL);

        TextView storeName = new TextView(mContext);
        storeName.setTextSize(22);
        storeName.setTextColor(Color.BLACK);
        storeName.setText(s.storeName);
        mainLyaout.addView(storeName);

        TextView storeCost = new TextView(mContext);
        storeCost.setTextSize(22);
        if (s.reachThreshold()){
            storeCost.setTextColor(Color.RED);
        }else {
            storeCost.setTextColor(Color.BLUE);
        }
        storeCost.setText(" ( " + s.getBuyCostWithDiscount() + " JPD )");
        mainLyaout.addView(storeCost);
        ll.addView(mainLyaout);

        TextView storeInfo = new TextView(mContext);
        storeInfo.setTextSize(18);
        storeInfo.setTextColor(Color.GRAY);
        storeInfo.setText("Threshold:" + s.threshould + ", Discount:" + s.discount);
        ll.addView(storeInfo);

        return ll;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        HashMap<String, Object> pInfo = shopProductsList.get(groupPosition).get(childPosition);
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);
        ll.setPadding(20, 10, 10, 10);


        TextView productName = new TextView(mContext);
        productName.setTextSize(20);
        productName.setTextColor(Color.BLACK);
        productName.setText((String) pInfo.get("NAME"));
        ll.addView(productName);

        TextView productInfo = new TextView(mContext);
        productInfo.setTextSize(16);
        productInfo.setTextColor(Color.GRAY);
        productInfo.setText("Price:" +(Integer) pInfo.get("PRICE") +", Amount:" + (Integer) pInfo.get("AMOUNT"));
        ll.addView(productInfo);

        return ll;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
