package com.example.brandon.jpbestbuy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;


public class AddStoreProduct extends AppCompatActivity {

    private final static String TAG = "AddStoreProduct";

    private int storeID;
    private Spinner spnProductList;
    ArrayList<HashMap<String, String>> productInfo;

    Dialog addProductDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_product);


        updateSpinnerProductList(false);
        initSpinnerProductList();

        initBtn();


    }

    private void initBtn() {
        Button btnSavePrice = (Button) findViewById(R.id.btnAddPriceSave);
        btnSavePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get product id
                Spinner spnProductList = (Spinner) findViewById(R.id.spn_product_list);
                Integer pos = spnProductList.getSelectedItemPosition();
                Integer pid = Integer.valueOf(productInfo.get(pos).get("_id"));
                Log.d(TAG, "select:" + pid);

                //get price
                EditText editPrice = (EditText) findViewById(R.id.editProductPrice);
                Integer price = Integer.valueOf(editPrice.getText().toString());
                //DB.addPrice(storeID, pid, price);

                //get Tax Status
                Spinner spnTaxList = (Spinner) findViewById(R.id.sp_new_product_tax);
                Integer tax = spnTaxList.getSelectedItemPosition();

                if (tax == 0) {
                    double d = price * 1.08;
                    Log.d(TAG, "New Price="+d);
                    Long L = Math.round(d);
                    price = Integer.valueOf(L.intValue());
                }

                Intent it = AddStoreProduct.this.getIntent();
                Bundle bundle = new Bundle();
//                bundle.putInt("pid", pid + 1);
                bundle.putInt("pid", pid);
                bundle.putInt("price", price);

                it.putExtras(bundle);
                AddStoreProduct.this.setResult(Activity.RESULT_OK, it);    //回傳RESULT_OK
                AddStoreProduct.this.finish();

            }
        });
        Button btnCancelPrice = (Button) findViewById(R.id.btnAddPriceCancel);
        btnCancelPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initSpinnerProductList(){
        spnProductList.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
                Cursor pCursor = DB.getAllProduct();
                //productInfo = Utils.cur2ArrayList(pCursor);
                ArrayList<String> productList = getProductList(pCursor);
                //Log.d(sCursor, "(Spinner) selected: " + position + "/" + productList.size());
                Integer pListSize = new Integer(productList.size());
                if (pListSize.equals((position + 1))) {
                    Log.d(TAG, "Add new product! ");
                    addProductDialog();
                }
            }

            public void onNothingSelected(AdapterView arg0) {
                Log.d(TAG, "Spinner nothind selected!!");
            }
        });
    }

    private void updateSpinnerProductList(boolean selectNewItem){
        spnProductList = (Spinner) findViewById(R.id.spn_product_list);
        Cursor pCursor= DB.getAllProduct();
        productInfo = Utils.cur2ArrayList(pCursor);
        ArrayList<String> productList = getProductList(pCursor);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, R.layout.spinner_adapter_product_list, R.id.text, productList);
        spnProductList.setAdapter(adapter);
        if (selectNewItem) {
            spnProductList.setSelection(productList.size() - 2);
        } else {
            spnProductList.setSelection(0);
        }
    }

    private ArrayList<String> getProductList(Cursor pCursor) {
        ArrayList<String> result = new ArrayList<String>();
        pCursor.moveToFirst();
        while (pCursor.isAfterLast() == false) {
            String pName = pCursor.getString(1);
            result.add(pName);
            //Log.d(sCursor, "Product Spinner: add " + pName);
            pCursor.moveToNext();
        }
        pCursor.moveToFirst();
	    result.add("Add Product!");
        return result;
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
        //btnSave.setOnClickListener(addProductSaveOnClkLis);
        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText newProduct = (EditText) addProductDlg.findViewById(R.id.et_addProductName);
                String pName = newProduct.getText().toString();
                EditText newAmount = (EditText) addProductDlg.findViewById(R.id.et_addProductAmount);
                Integer pAmount = Integer.valueOf(newAmount.getText().toString());
                DB.addProduct(pName, pAmount);
                Log.d(TAG, "Add new product: " + pName);
                updateSpinnerProductList(true);
                addProductDlg.cancel();
            }
        });
        addProductDlg.show();
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
