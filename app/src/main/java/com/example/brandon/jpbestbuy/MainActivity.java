package com.example.brandon.jpbestbuy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Database db;

    private final static String TAG = Utils.TAG + "(main)";

    Dialog addStoreDlg;
    Dialog delStoreDlg;

    private ArrayList<String> storeArray;
    private SwipeListView mSwipeListView ;
    private SwipeAdapter mAdapter ;
    public static int deviceWidth ;
    //private ArrayList<HashMap<String, String>> storeList;

    private boolean getGPSService = false;
    LocationManager locationManager;
    private String bestProvider = LocationManager.GPS_PROVIDER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initial Database */
        DB.initDatabase(this);

	    /* put test data */
	    if (noMockupInit()) {
		    initMockupCase1();
	    }

        initSwipeListView();

    }

    private ArrayList<HashMap<String, String>> getStoreData() {
        Cursor cursor = DB.getStore();
        return Utils.cur2ArrayList(cursor);
    }

    private List<String> getStoreNameList(ArrayList<HashMap<String, String>> storeList) {
        List<String> items = new ArrayList<String>();
        for (HashMap<String, String>store : storeList){
            items.add(store.get("NAME"));
        }
        Log.d(TAG, "storeNameList:" + items.toString());
        return items;
    }

    private int getDeviceWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }

    private void initSwipeListView() {
        ArrayList<HashMap<String, String>> storeList = getStoreData();
        mSwipeListView = (SwipeListView) findViewById(R.id.swipe_list_main);
        mAdapter = new SwipeAdapter(this, R.layout.package_row, storeList, getStoreNameList(storeList), mSwipeListView);
        mSwipeListView.setAdapter(mAdapter);
        deviceWidth = getDeviceWidth();
        mSwipeListView.setSwipeListViewListener(new TestBaseSwipeListViewListener());
        mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
        mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
//                mSwipeListView.setSwipeActionRight(settings.getSwipeActionRight());
        mSwipeListView.setOffsetLeft(deviceWidth * 1 / 3);
//                mSwipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        //mSwipeListView.setAnimationTime(1);
        mSwipeListView.setSwipeOpenOnLongPress(false);
    }

    private void updateSwipeData(){
        if (mSwipeListView != null) {
            ArrayList<HashMap<String, String>> storeList = getStoreData();
            mAdapter = new SwipeAdapter(this, R.layout.package_row, storeList, getStoreNameList(storeList), mSwipeListView);
            mSwipeListView.setAdapter(mAdapter);
        }
    }

    class TestBaseSwipeListViewListener extends BaseSwipeListViewListener {

        @Override
        public void onClickFrontView(int position) {
            super.onClickFrontView(position);
            Log.d(TAG, "[StoreList] select " + position + " store");

                // go to store activity.
            Intent it = new Intent(MainActivity.this, StoreInfo.class);
            it.putExtra("Position", position);
            ArrayList<HashMap<String, String>> storeList = getStoreData();
            it.putExtra("jsonStoreObj", Utils.hashmap2Json(storeList.get(position)).toString());
            startActivity(it);

        }

        @Override
        public void onDismiss(int[] reverseSortedPositions) {
            for (int position : reverseSortedPositions) {
                ArrayList<HashMap<String, String>> storeList = getStoreData();
                Log.d(TAG, "delete store" + storeList.get(position));
            }
            updateSwipeData();
        }

    }


	public boolean noMockupInit() {
        /**
         * if key "inited" exist in sharePreference,
         * then return false.
         * else return true.
         */
		String key = "mockup_inited";
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		return (!pref.getBoolean(key, false));

	}

    public void setPreference(String key, Boolean value) {
	    Log.d(TAG, "setPreference");
        SharedPreferences.Editor prefEditor = getPreferences(MODE_PRIVATE).edit();
        prefEditor.putBoolean(key, value);
        prefEditor.commit();

    }


	private void initMockupCase1() {
		setPreference("mockup_inited", true);

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

        String addr = Utils.getAddressFromLocation(this, Double.valueOf("25.0497191"), Double.valueOf("121.5747108"));
        DB.addStore("Sun drug", 5400, 8, "25.0497191,121.5747108;"+addr);
		DB.addPrice(2, 1, 150);
		DB.addPrice(2, 2, 250);
		DB.addPrice(2, 3, 3000);
		DB.addPrice(2, 5, 550);
        DB.addPrice(2, 4, 2400);

	}


    public void addStoreDialog(View v){

        addStoreDlg = new Dialog(this);
        addStoreDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        addStoreDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addStoreDlg.setContentView(R.layout.dialog_add_store);

        Location location = getGPSLocation();
        if (location != null) {
            TextView locationTextView = (TextView) addStoreDlg.findViewById(R.id.tv_add_store_GPS);
            String latitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
            String longitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
            String locationString = latitude
                    + "," + longitude
                    + ";" + Utils.getAddressFromLocation(this, Double.valueOf(latitude), Double.valueOf(longitude));
            locationTextView.setText(locationString);
        }else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
            Log.d(TAG,"無法定位座標");
        }


        Button btnCancel = (Button)addStoreDlg.findViewById(R.id.btnAddStoreCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                addStoreDlg.cancel();
            }
        });

        Button btnSave = (Button)addStoreDlg.findViewById(R.id.btnAddStoreSave);
        btnSave.setOnClickListener(addStoreSaveOnClkLis);
        addStoreDlg.show();
    }


    private Location getGPSLocation(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
            Criteria criteria = new Criteria();	//資訊提供者選取標準
            bestProvider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            return location;
        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }
        return null;
    }

    private Button.OnClickListener addStoreSaveOnClkLis = new Button.OnClickListener() {
        public void onClick(View v) {
            EditText newStoreName = (EditText) addStoreDlg.findViewById(R.id.editStoreName);
            EditText newThreshold = (EditText) addStoreDlg.findViewById(R.id.editThreshold);
            EditText newDiscount = (EditText) addStoreDlg.findViewById(R.id.editDiscount);
            TextView newLocation = (TextView) addStoreDlg.findViewById(R.id.tv_add_store_GPS);
            String newStoreNameStr = newStoreName.getText().toString();
            Integer newThresholdInt = Integer.valueOf(newThreshold.getText().toString());
            Integer newDiscountInt = Integer.valueOf(newDiscount.getText().toString());
            String newLocationStr = newLocation.getText().toString();
            Log.d(TAG, "add new store: " + newStoreNameStr + newThresholdInt + newDiscountInt);
            DB.addStore(newStoreNameStr, newThresholdInt, newDiscountInt, newLocationStr);
            //updateStoreList();

            updateSwipeData();
            addStoreDlg.cancel();
        }
    };


    public void goSelectStore(View v){
        Intent it = new Intent(MainActivity.this, SelectStoreToComputeActivity.class);
        startActivity(it);
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
                //it = new Intent(MainActivity.this, TestList1.class);
                //startActivity(it);
                break;
            case R.id.menu_map:
                Log.d(TAG, "select menu item: Maps");
                it = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(it);
                break;
            case R.id.menu_shoplist:
                Log.d(TAG, "select menu item: Shopping List");
                //it = new Intent(MainActivity.this, TestList1.class);
                //startActivity(it);
                break;

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void cleanMockupCase(){
        Log.d(TAG,"clean SQL and SharedPreferences data of MockupCase");
        SharedPreferences.Editor prefEditor = getPreferences(MODE_PRIVATE).edit();
        prefEditor.clear();
        prefEditor.commit();
    }

    @Override
    public void onDestroy(){
        //cleanMockupCase();
        Log.d(TAG, "Bye~");
        super.onDestroy();
    }

}
