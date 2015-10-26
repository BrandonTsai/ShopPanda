package com.example.brandon.jpbestbuy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ShopListActivity extends AppCompatActivity {

    private static final String TAG="ShopList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        Log.d(TAG, "onCreate!");
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
            case R.id.menu_shoplist:
                Log.d(TAG, "select menu item: Shopping List");
                it = new Intent(this, ShopListActivity.class);
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
