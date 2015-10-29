package com.example.brandon.jpbestbuy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
    Dialog addProductDlg;

    private ArrayList<String> storeArray;
    private SwipeListView mSwipeListView ;
    private SwipeAdapter mAdapter ;
    public static int deviceWidth ;
    //private ArrayList<HashMap<String, String>> storeList;

    View frameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initial Database */
        DB.initDatabase(this);

	    /* put test data */
	    if (Mockup.noMockupInit(this)) {
            Mockup.initMockupCase1(this);
	    }

        initButton();
        showStoreFrameView();
        //getLatlngFromAddr();
    }

    private void initButton() {
        Button showStoreBtn = (Button)findViewById(R.id.button_store_list);
        showStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStoreFrameView();
            }
        });


        Button showProductBtn = (Button)findViewById(R.id.button_product_list);
        showProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductFrameView();
            }
        });

    }


    public void showStoreFrameView(){
        Button showStoreBtn = (Button)findViewById(R.id.button_store_list);
        showStoreBtn.setBackgroundColor(Color.parseColor("#efeded"));
        showStoreBtn.setTextColor(Color.parseColor("#279a42"));
        Button showProductBtn = (Button)findViewById(R.id.button_product_list);
        showProductBtn.setBackgroundColor(Color.LTGRAY);
        showProductBtn.setTextColor(Color.BLACK);

        ViewGroup frameViewHolder = (ViewGroup)findViewById(R.id.main_frameLayout);
        if (frameView != null) {
            frameViewHolder.removeView(frameView);
        }

        frameView = getLayoutInflater().inflate(R.layout.activity_store_list, null);
        frameViewHolder.addView(frameView);

        initStoreListView();

    }

    public void showProductFrameView(){
        Button showStoreBtn = (Button)findViewById(R.id.button_store_list);
        showStoreBtn.setBackgroundColor(Color.LTGRAY);
        showStoreBtn.setTextColor(Color.BLACK);
        Button showProductBtn = (Button)findViewById(R.id.button_product_list);
        showProductBtn.setBackgroundColor(Color.parseColor("#efeded"));
        showProductBtn.setTextColor(Color.parseColor("#279a42"));

        ViewGroup frameViewHolder = (ViewGroup)findViewById(R.id.main_frameLayout);
        if (frameView != null) {
            frameViewHolder.removeView(frameView);
        }

        frameView = getLayoutInflater().inflate(R.layout.activity_product_list, null);
        frameViewHolder.addView(frameView);

        initProductListView();
    }




    private void initStoreListView() {
        ArrayList<HashMap<String, String>> storeList = getStoreData();
        mSwipeListView = (SwipeListView) frameView.findViewById(R.id.swipe_list_main);
        mAdapter = new SwipeAdapter(this, R.layout.package_row, storeList, getStoreNameList(storeList), mSwipeListView);
        mSwipeListView.setAdapter(mAdapter);
        deviceWidth = Utils.getDeviceWidth(this);
        mSwipeListView.setSwipeListViewListener(new TestBaseSwipeListViewListener());
        mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        //mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        mSwipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        //mSwipeListView.setOffsetLeft(deviceWidth * 1 / 3);
        mSwipeListView.setOffsetRight(deviceWidth * 1 / 2);
        //mSwipeListView.setAnimationTime(1);
        mSwipeListView.setSwipeOpenOnLongPress(false);



        Button addStoreBtn = new Button(this);
        addStoreBtn.setText("+");
        addStoreBtn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        addStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStoreDialog(v);
            }
        });
        addStoreBtn.setBackgroundColor(Color.parseColor("#C7DFBA"));
        mSwipeListView.addFooterView(addStoreBtn);


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

    public void addStoreDialog(View v){

        addStoreDlg = new Dialog(this);
        addStoreDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        addStoreDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addStoreDlg.setContentView(R.layout.dialog_add_store);

        Location location = Utils.getGPSLocation((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        if (location != null) {
            TextView locationTextView = (TextView) addStoreDlg.findViewById(R.id.tv_add_store_GPS);
            String latitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
            String longitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
            String locationString = latitude
                    + "," + longitude
                    + ";" + Utils.getAddressFromLocation(this, Double.valueOf(latitude), Double.valueOf(longitude));
            locationTextView.setText(locationString);
        }else {
            Toast.makeText(this, "無法定位座標! 請開啟定位服務!", Toast.LENGTH_SHORT).show();
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


    public void initProductListView() {
        Log.d(TAG, "initProductListView()");

        SwipeListView mSwipeListView = (SwipeListView) frameView.findViewById(R.id.swipe_listView_productlist);
        Button addProductBtn = new Button(this);
        addProductBtn.setText("+");
        addProductBtn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductDialog();
            }
        });
        addProductBtn.setBackgroundColor(Color.parseColor("#C7DFBA"));
        mSwipeListView.addFooterView(addProductBtn);



        int deviceWidth = getResources().getDisplayMetrics().widthPixels;
        //mSwipeListView.setSwipeListViewListener(new TestBaseSwipeListViewListener());
        mSwipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_RIGHT);
        //mSwipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        mSwipeListView.setSwipeActionRight(SwipeListView.SWIPE_ACTION_REVEAL);
        mSwipeListView.setOffsetRight(deviceWidth * 1 / 3);
//                mSwipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        //mSwipeListView.setAnimationTime(1);
        mSwipeListView.setSwipeOpenOnLongPress(false);
        updateShopList();

    }

    public void addProductDialog(){
        addProductDlg = new Dialog(this);
        addProductDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        addProductDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addProductDlg.setContentView(R.layout.dialog_add_product);

        Button btnCancel = (Button)addProductDlg.findViewById(R.id.dlgBtnAddProductCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                addProductDlg.cancel();
            }
        });

        Button btnSave = (Button)addProductDlg.findViewById(R.id.dlgBtnAddProductSave);
        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText newProduct = (EditText) addProductDlg.findViewById(R.id.et_addProductName);
                String pName = newProduct.getText().toString();
                EditText newAmount = (EditText) addProductDlg.findViewById(R.id.et_addProductAmount);
                Integer pAmount = Integer.valueOf(newAmount.getText().toString());
                DB.addProduct(pName, pAmount);
                Log.d(TAG, "Add new product: " + pName);
                updateShopList();
                addProductDlg.cancel();
            }
        });
        addProductDlg.show();
    }

    private void updateShopList() {
        SwipeListView mSwipeListView = (SwipeListView) frameView.findViewById(R.id.swipe_listView_productlist);
        Cursor c = DB.getAllProduct();
        mSwipeListView.setAdapter(new ShopListAdapter(this, c));
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




    @Override
    public void onDestroy(){
        //cleanMockupCase();
        Log.d(TAG, "Bye~");
        super.onDestroy();
    }


    private void getLatlngFromAddr(){

        int[] src  = {
////                R.array.store_curry,
////                R.array.store_noodle,
//                R.array.store_pancake,
//                R.array.store_toast,
//                R.array.store_shop_stores
////                R.array.hotspots
        };

        for (int res:src){
            String[] mStoreArray = getResources().getStringArray(res);
            String out="";
            for (String s: mStoreArray){
                if (s.contains("#")){
                    Log.d(TAG,">>"+s);
                    String[] sInfo = s.split("#");
                    String name = sInfo[0];
                    String addr = sInfo[1];
                    Double[] latlng = Utils.getLatLngFromAddr(this, addr);
                    out += "<item>"+latlng[0]+","+latlng[1]+"@"+name+"@"+addr+"</item>";
                }
            }
            Log.d(TAG,out);
        }

    }

}
