package com.example.brandon.jpbestbuy;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG="Map";
    private GoogleMap mMap;
    //private LatLng currentLatLng;
    private int currentCategory = 0;
    public ArrayList<LinkedHashMap<String, Integer>> storeResIds;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_maps();

        init_spinner();

        //init_current_location();

    }


    private void init_maps() {
        setContentView(R.layout.activity_store_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    private void initStoreResIds(){
        storeResIds = new ArrayList<>();

        LinkedHashMap<String, Integer> drugStoresResIds = new LinkedHashMap<>();
        drugStoresResIds.put("OS Drug", R.array.store_os_drug);
        drugStoresResIds.put("X Drug", R.array.store_x_drug);
        storeResIds.add(drugStoresResIds);

        LinkedHashMap<String, Integer> marketStoresResIds = new LinkedHashMap<>();
        marketStoresResIds.put("2 Drug", R.array.store_os_drug);
        storeResIds.add(marketStoresResIds);

        LinkedHashMap<String, Integer> electronicStoresResIds = new LinkedHashMap<>();
        electronicStoresResIds.put("3 Drug", R.array.store_x_drug);
        storeResIds.add(electronicStoresResIds);

        LinkedHashMap<String, Integer> clothesStoresResIds = new LinkedHashMap<>();
        clothesStoresResIds.put("4 Drug", R.array.store_os_drug);
        clothesStoresResIds.put("5 Drug", R.array.store_x_drug);
        storeResIds.add(clothesStoresResIds);
    }

    private void init_spinner(){
        initStoreResIds();

        Spinner categorySpinner = (Spinner) findViewById(R.id.map_spinner_category);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currentCategory = position;
                Log.d(TAG, "OnCategoryItemSelect" + currentCategory);

                ArrayList<String> spinnerItems = getStoresListOfCategory(position);
                Spinner storeSpinner = (Spinner) findViewById(R.id.map_spinner_store);
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_item, spinnerItems);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                storeSpinner.setAdapter(spinnerArrayAdapter);

                ArrayList<HashMap<String, String>> stores = getCategoryAllStoresInfo(position);
                updateMapMarker(stores);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mMap.clear();
            }
        });

        Spinner storeSpinner = (Spinner) findViewById(R.id.map_spinner_store);
        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<HashMap<String, String>> stores = getOneStoresInfo(currentCategory, position);
                updateMapMarker(stores);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    private ArrayList<String> getStoresListOfCategory(int position) {
        ArrayList<String> sList = new ArrayList<>();
        sList.add("All");
        Log.d(TAG, position + " keyset:" + storeResIds.get(position).keySet());
        for (String storename:storeResIds.get(position).keySet()){
            sList.add(storename);
        }
        return sList;
    }

    private ArrayList<HashMap<String,String>> getOneStoresInfo(int currentCategory, int position) {
        if (position == 0){
            return getCategoryAllStoresInfo(currentCategory);
        }
        position = position-1;
        Integer storesResID = (new ArrayList<Integer>(storeResIds.get(currentCategory).values())).get(position);

        ArrayList<HashMap<String, String>> stores = new ArrayList<>();
        for (String storeInfo:getResources().getStringArray(storesResID)){
            String[] sInfo = storeInfo.split("@");
            String[] latlng = sInfo[0].split(",");
            String name = sInfo[1];
            String addr = sInfo[2];
            HashMap<String, String> store = new HashMap<>();
            store.put("name", name);
            store.put("lat", latlng[0]);
            store.put("lng", latlng[1]);
            stores.add(store);
            Log.d(TAG, "mark:" +name);
        }


        return stores;
    }

    private ArrayList<HashMap<String, String>> getCategoryAllStoresInfo(int position) {
        ArrayList<HashMap<String, String>> stores = new ArrayList<HashMap<String, String>>();
        for (int storesResID: storeResIds.get(position).values()){
            for (String storeInfo:getResources().getStringArray(storesResID)){
                String[] sInfo = storeInfo.split("@");
                String[] latlng = sInfo[0].split(",");
                String name = sInfo[1];
                String addr = sInfo[2];
                HashMap<String, String> store = new HashMap<>();
                store.put("name", name);
                store.put("lat", latlng[0]);
                store.put("lng", latlng[1]);
                stores.add(store);
                Log.d(TAG, "mark:" +name);
            }

        }
        return stores;
    }

    private void updateMapMarker(ArrayList<HashMap<String, String>> stores) {

        mMap.clear();
        for (HashMap<String,String> s:stores){
            String name = s.get("name");
            Double lat = Double.valueOf(s.get("lat"));
            Double lng = Double.valueOf(s.get("lng"));
            LatLng storeLatlng = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(storeLatlng).title(name));
            Log.d(TAG, "mMap+:" + name);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        mMap.clear();
        LatLng tokeyTower = new LatLng(35.658554, 139.745572);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokeyTower, 13));
        mMap.setMyLocationEnabled(true);

    }


    //    private void get_current_location() {
//        Location location = Utils.getGPSLocation((LocationManager) getSystemService(Context.LOCATION_SERVICE));
//        if (location != null){
//            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//        }else {
//            //default sydney.
//            currentLatLng = new LatLng(-34, 151);
//        }
//    }
}
