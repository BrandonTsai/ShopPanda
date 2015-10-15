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
//        SelectStoreListAdapter storeListAdapter = new SelectStoreListAdapter(this, stores);
//        storeList.setAdapter(storeListAdapter);
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

//    public void goComputeResult(View v){
//        ArrayList<HashMap<String, String>> selectStores = new ArrayList<>();
//
//        SparseBooleanArray sparseBooleanArray = storeList.getCheckedItemPositions();
//        for (int i = 0 ; i <stores.size(); i++){
//            if (sparseBooleanArray.get(i)){
//               selectStores.add(stores.get(i));
//            }
//        }
//        Log.d(TAG, "Select stores:"+selectStores.toString());
//
//        if (verifySelects(selectStores)) {
//            Intent it = new Intent(SelectStoreToComputeActivity.this, ComputeBestBuy2.class);
//            Bundle bundle = new Bundle();
//            bundle.putStringArrayList("storeIds", getStoreIDs(selectStores));
//            it.putExtras(bundle);
//            startActivity(it);
//        }
//    }

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
        //ComputeBestShopList newCompute = new ComputeBestShopList();
        //newCompute.getBestShopList(selectStores);
    }


//    private ArrayList<String> getStoreIDs(ArrayList<HashMap<String, String>> selectStores) {
//        ArrayList<String> storeIds = new ArrayList<>();
//        for (HashMap<String, String> store : selectStores){
//            storeIds.add(store.get("_id"));
//        }
//        return storeIds;
//    }

//    private boolean verifySelects(ArrayList<HashMap<String, String>> selectStores) {
//
//        if (selectStores.size()<2){
//            Toast.makeText(SelectStoreToComputeActivity.this, "You have select at least 2 stores", Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        ArrayList<String> noProductStoreNames = new ArrayList<>();
//        for (HashMap<String, String> store: selectStores){
//            int sid = Integer.valueOf(store.get("_id"));
//            Cursor allPrice = DB.getAllPrice(sid);
//            if(allPrice==null || allPrice.getCount()==0){
//                noProductStoreNames.add(store.get("NAME"));
//            }
//        }
//
//        if (noProductStoreNames.size()>0){
//            Toast.makeText(SelectStoreToComputeActivity.this,
//                    "Following Stores has no product:" + noProductStoreNames.toString(),
//                    Toast.LENGTH_LONG).show();
//            return false;
//        }
//
//        return true;
//    }

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
