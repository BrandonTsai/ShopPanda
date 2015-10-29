package com.example.brandon.jpbestbuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    public void returnToPreActivity(View v){
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        // Go to different intent if item selected
        Intent it = null;
        switch (id){
            case R.id.menu_main:
                Log.d(TAG, "select menu item: main");
                it = new Intent(this, MainActivity.class);
                startActivity(it);
                break;
            case R.id.menu_map:
                Log.d(TAG, "select menu item: Maps");
                it = new Intent(this, MapsActivity.class);
                startActivity(it);
                break;


        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
