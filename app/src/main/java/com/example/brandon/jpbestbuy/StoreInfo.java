package com.example.brandon.jpbestbuy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;


public class StoreInfo extends AppCompatActivity {

    final String TAG = Utils.TAG + "(EditStore)";
    Context context;

    int postition = 0;
    JSONObject jsonStore;
    JSONArray productPriceList;

    Dialog updateProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store);
        context = this;

        try {
            Intent it = getIntent();
            postition = it.getIntExtra("Position", 0);
            jsonStore = new JSONObject(it.getStringExtra("jsonStoreObj"));
            Log.d(TAG, "get Store: " + jsonStore.toString());

            TextView storename = (TextView) findViewById(R.id.tv_editstore_storename);
            storename.setText(jsonStore.getString("NAME"));
            TextView threshold = (TextView) findViewById(R.id.tv_editstore_threshold);
            threshold.setText(jsonStore.getString("THRESHOLD"));
            TextView discount = (TextView) findViewById(R.id.tv_editstore_discount);
            discount.setText(jsonStore.getString("DISCOUNT"));

            TextView locationTextView = (TextView) findViewById(R.id.tv_storeinfo_location);
            String location = jsonStore.getString("LOCATION");

            if (!location.equals("Null")) {

                String[] lc = location.split(";");
                String[] gps = lc[0].split(",");
                Double latitude = Double.valueOf(gps[0]);
                Double longitude = Double.valueOf(gps[1]);

                //locationTextView.setText(getAddressFromLocation(latitude,longitude));
                if (lc[1] != null){
                    locationTextView.setText(lc[1]);
                } else {
                    locationTextView.setText(location);
                }
                locationTextView.setOnClickListener(new LocationOnClickListener());
            } else {
                locationTextView.setText("Cat not get location!");
                locationTextView.setTextColor(Color.LTGRAY);
            }



            initStoreProductPriceList();
            updateStoreProductPriceList();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected class LocationOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                String location = jsonStore.getString("LOCATION");
                String[] lc = location.split(";");
                String[] gps = lc[0].split(",");
                Double latitude = Double.valueOf(gps[0]);
                Double longitude = Double.valueOf(gps[1]);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=", latitude, longitude);
                if (lc[1] != null) {
                    uri = uri + lc[1];
                }
                else {
                    uri = uri + Utils.getAddressFromLocation(StoreInfo.this, latitude, longitude);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initStoreProductPriceList() {
        ListView lvProductPrice = (ListView) findViewById(R.id.lv_store_product_price);
        Button addProductBtn = new Button(this);
        addProductBtn.setText("Add New Product");
        addProductBtn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStoreProduct(v);
            }
        });
//        addProductBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.lightGreen)));
//        addProductBtn.setBackgroundTintMode(PorterDuff.Mode.DST_ATOP);
        lvProductPrice.addFooterView(addProductBtn);
        MyOnItemLongClickListener mOnItemLongClickListener = new MyOnItemLongClickListener();
        lvProductPrice.setOnItemLongClickListener(mOnItemLongClickListener);
    }



    public class MyOnItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                editProductDialog(view, position);
                updateStoreProductPriceList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public void editProductDialog(View v, int position) throws JSONException {

        JSONObject priceInfo = productPriceList.getJSONObject(position);
        HashMap<String,String> productInfo = DB.getProduct(priceInfo.getInt("PID"));

        updateProduct = new Dialog(this);
        updateProduct.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        updateProduct.requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateProduct.setContentView(R.layout.dialog_edit_product);

        TextView sid = (TextView) updateProduct.findViewById(R.id.tv_editProduct_sid);
        sid.setText(String.valueOf(priceInfo.getInt("SID")));

        TextView pid = (TextView) updateProduct.findViewById(R.id.tv_editProduct_pid);
        pid.setText(String.valueOf(priceInfo.getInt("PID")));

        TextView productName = (TextView) updateProduct.findViewById(R.id.tv_editProductName);
        productName.setText(productInfo.get("NAME"));

        TextView productAmount = (TextView) updateProduct.findViewById(R.id.tv_editProductAmount);
        productAmount.setText(productInfo.get("AMOUNT"));

        EditText productPrice = (EditText) updateProduct.findViewById(R.id.et_editProductPrice);
        productPrice.setText(String.valueOf(priceInfo.getInt("PRICE")));

        Spinner spnTaxList = (Spinner) updateProduct.findViewById(R.id.sp_edit_price_tax);
        spnTaxList.setSelection(1);

        Button btnCancel = (Button)updateProduct.findViewById(R.id.dlgBtnEditProductCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                updateProduct.cancel();
            }
        });

        Button btnSave = (Button)updateProduct.findViewById(R.id.dlgBtnEditProductSave);
        btnSave.setOnClickListener(editProductSaveOnClkLis);

        Button btnDel = (Button)updateProduct.findViewById(R.id.dlgBtnEditProductDelete);
        btnDel.setOnClickListener(editProductDeleteOnClkLis);

        updateProduct.show();
    }


    private Button.OnClickListener editProductSaveOnClkLis = new Button.OnClickListener() {
        public void onClick(View v) {
            TextView sidTextView = (TextView) updateProduct.findViewById(R.id.tv_editProduct_sid);
            int sid = Integer.valueOf(sidTextView.getText().toString());

            TextView pidTextView = (TextView) updateProduct.findViewById(R.id.tv_editProduct_pid);
            int pid = Integer.valueOf(pidTextView.getText().toString());

            EditText priceEditText = (EditText) updateProduct.findViewById(R.id.et_editProductPrice);
            int price = Integer.valueOf(priceEditText.getText().toString());
            Log.d(TAG, "update product price: " + price);

            Spinner spnTaxList = (Spinner) updateProduct.findViewById(R.id.sp_edit_price_tax);
            Integer tax = spnTaxList.getSelectedItemPosition();

            if (tax == 0) {
                double d = price * 1.08;
                Log.d(TAG, "New Price="+d);
                Long L = Math.round(d);
                price = Integer.valueOf(L.intValue());
            }


            DB.updatePrice(sid, pid, price);
            try {
                updateStoreProductPriceList();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateProduct.cancel();
        }
    };


    private Button.OnClickListener editProductDeleteOnClkLis = new Button.OnClickListener() {
        public void onClick(View v) {
            TextView sidTextView = (TextView) updateProduct.findViewById(R.id.tv_editProduct_sid);
            int sid = Integer.valueOf(sidTextView.getText().toString());

            TextView pidTextView = (TextView) updateProduct.findViewById(R.id.tv_editProduct_pid);
            int pid = Integer.valueOf(pidTextView.getText().toString());

            Log.d(TAG, "delete product: " + pid);
            DB.deletePrice(sid, pid);
            try {
                updateStoreProductPriceList();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateProduct.cancel();
        }
    };


    private void updateStoreProductPriceList() throws JSONException {
        ListView lvProductPrice = (ListView) findViewById(R.id.lv_store_product_price);
        Cursor c = DB.getAllPrice(jsonStore.getInt("_id"));
        productPriceList = Utils.cur2Json(c);
        Log.d(TAG, "update product price list:" + productPriceList.toString());
        ProductPriceListAdapter adapter = new ProductPriceListAdapter(this, c);
        // Bind to our new adapter.
        lvProductPrice.setAdapter(adapter);


    }

    public void addStoreProduct(View v){

        try {
            String sid = jsonStore.getString("_id");
            Intent it = new Intent(StoreInfo.this, AddStoreProduct.class);
            //it.putExtra("SID", sid);
            Log.d(TAG, "add product to SID:" + sid);
            startActivityForResult(it, 0);
            //startActivity(it);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            try {
                Bundle bundle = data.getExtras();
                Integer pid = Integer.valueOf(bundle.getInt("pid"));
                Integer price = Integer.valueOf(bundle.getInt("price"));
                Log.d(TAG,"add price:"+ jsonStore.getInt("_id") +pid+price);
                DB.addPrice(jsonStore.getInt("_id"), pid, price);
                updateStoreProductPriceList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //txt_hello.setText(bundle.getString("name2"));
        }
    }


    public void goBack(View v){
        finish();
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
