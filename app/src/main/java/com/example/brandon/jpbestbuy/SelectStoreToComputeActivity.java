package com.example.brandon.jpbestbuy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectStoreToComputeActivity extends AppCompatActivity {

    private final static String TAG = "SelectStore";

    private ArrayList<HashMap<String, String>> stores;

    ListView storeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_store_to_compute);

        stores = DB.getStoreArrayList();

        storeList = (ListView) findViewById(R.id.lv_select_store_check_list);
        storeList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        String[] listContent = getStoreNames(stores);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, listContent);
        storeList.setAdapter(adapter);

    }

    private String[] getStoreNames(ArrayList<HashMap<String, String>> stores){
        String[] storeNames = new String[stores.size()];
        for (int i = 0 ; i <stores.size(); i++){
            storeNames[i] = stores.get(i).get("NAME");
        }
        return storeNames;
    }


    public  void getComputedResult2(View v){

        HashMap<Integer, Store> selectStores = new HashMap<>();
        ArrayList<String> storeIds = new ArrayList<>();
        SparseBooleanArray sparseBooleanArray = storeList.getCheckedItemPositions();
        for (int i = 0 ; i <stores.size(); i++){
            if (sparseBooleanArray.get(i)){
                Store store = new Store(stores.get(i));
                selectStores.put(store.sid, store);
                storeIds.add(String.valueOf(store.sid));
            }
        }

        if (verifySelects(selectStores)) {
            Log.d(TAG, "Go Compute Result");
            Intent it = new Intent(SelectStoreToComputeActivity.this, BestShopResult.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("storeIds", storeIds);
            it.putExtras(bundle);
            startActivity(it);
        }
    }




    private boolean verifySelects(HashMap<Integer, Store> selectStores) {

        if (selectStores.size()<2){
            Toast.makeText(SelectStoreToComputeActivity.this, "You have select at least 2 stores", Toast.LENGTH_LONG).show();
            return false;
        }

        ArrayList<String> noProductStoreNames = new ArrayList<>();
        for (Store s: selectStores.values()){
            if (s.products.size() == 0){
                noProductStoreNames.add(s.storeName);
            }
        }

        if (noProductStoreNames.size()>0){
            Toast.makeText(SelectStoreToComputeActivity.this,
                    "Following Stores has no product:\n" + noProductStoreNames.toString().replace("[","").replace("]",""),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void cancelCompute(View v){
        finish();
    }
}
