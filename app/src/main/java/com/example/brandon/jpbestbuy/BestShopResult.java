package com.example.brandon.jpbestbuy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class BestShopResult extends AppCompatActivity {

    private static final String TAG="BestShopResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_shop_result);

        Bundle bundle = this.getIntent().getExtras();
        ArrayList<String> storeIds = (ArrayList<String>) bundle.get("storeIds");
        Log.d(TAG, "selected Stores:" + storeIds.toString());

        ComputeBestShopList computeBestShopList = new ComputeBestShopList();
        HashMap<Integer, Store> bestShopList = computeBestShopList.getBestShopList(storeIds);
        showResult(bestShopList);
    }

    private void showResult(HashMap<Integer, Store> shopList){

        int totalCost = 0;
        for (Store s: shopList.values()){
            totalCost += s.getBuyCostWithDiscount();
        }

        TextView bestCost = (TextView) findViewById(R.id.shopresult_tv_best_cost);
        bestCost.setText("Total Cost: " + String.valueOf(totalCost) + " (JPD)");


        ExpandableListView elv = (ExpandableListView)findViewById(R.id.shopresult_expListView);
        ExpandableStoreListAdapter viewAdapter = new ExpandableStoreListAdapter(this, shopList);
        elv.setAdapter(viewAdapter);
        for ( int i = 0; i < shopList.size(); i++ ) {
            elv.expandGroup(i);
        }
    }

}
