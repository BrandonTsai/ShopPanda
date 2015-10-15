package com.example.brandon.jpbestbuy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

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
        computeBestShopList.getBestShopList(storeIds);
    }
}
