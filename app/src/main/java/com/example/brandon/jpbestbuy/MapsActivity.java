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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
    private ArrayList<HashMap<String, String>> selectedStores;
    private Marker lastOpenned = null;

    public String[] categoryArray = {
            "All",
            "景點",
            "Food",
            "Cloth",
            "輕井澤 Karuizawa"
    };

    public int[] foodStoresResID = {
            R.array.hotspot_karuizawa_food,
            R.array.store_fruit_ice,
            R.array.store_toast,
            R.array.store_pancake,
            R.array.store_noodle,
            R.array.store_curry
    };

    public int[] clothStoresResID = {
            R.array.store_miia,
            R.array.store_skechers,
            R.array.store_shop_stores
    };


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

//        LinkedHashMap<String, Integer> drugStoresResIds = new LinkedHashMap<>();
//        drugStoresResIds.put("マツモトキヨシ(松本清)", R.array.store_matsukiyo);
//        storeResIds.add(drugStoresResIds);

        LinkedHashMap<String, Integer> hotspotResIds = new LinkedHashMap<>();
        hotspotResIds.put("其他", R.array.hotspots);
        hotspotResIds.put("楓葉", R.array.hotspots_maple_leaf);

        storeResIds.add(hotspotResIds);

        LinkedHashMap<String, Integer> foodStoresResIds = new LinkedHashMap<>();
        foodStoresResIds.put("鮮果甜品店", R.array.store_fruit_ice);
        foodStoresResIds.put("吐司", R.array.store_toast);
        foodStoresResIds.put("鬆餅", R.array.store_pancake);
        foodStoresResIds.put("咖哩", R.array.store_curry);
        foodStoresResIds.put("拉麵", R.array.store_noodle);
        storeResIds.add(foodStoresResIds);


        LinkedHashMap<String, Integer> clothesStoresResIds = new LinkedHashMap<>();
        clothesStoresResIds.put("MIIA", R.array.store_miia);
        clothesStoresResIds.put("Skechers", R.array.store_skechers);
        clothesStoresResIds.put("Others", R.array.store_shop_stores);
        storeResIds.add(clothesStoresResIds);

        LinkedHashMap<String, Integer> karuizawaResIds = new LinkedHashMap<>();
        karuizawaResIds.put("餐廳", R.array.hotspot_karuizawa_food);
        karuizawaResIds.put("景點", R.array.hotspot_karuizawa_attractions);
        storeResIds.add(karuizawaResIds);

    }

    private void init_spinner(){
        initStoreResIds();

        Spinner categorySpinner = (Spinner) findViewById(R.id.map_spinner_category);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                currentCategory = position;
                Log.d(TAG, "OnCategoryItemSelect" + currentCategory);

                if (position == 0) {
                    ArrayList<String> spinnerItems = getAllStoresList();
                    Spinner storeSpinner = (Spinner) findViewById(R.id.map_spinner_store);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_item, spinnerItems);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    storeSpinner.setAdapter(spinnerArrayAdapter);

                    ArrayList<HashMap<String, String>> stores = getAllStoresInfo();
                    updateMapMarker(stores);
                } else {
                    position -= 1;
                    ArrayList<String> spinnerItems = getStoresListOfCategory(position);
                    Spinner storeSpinner = (Spinner) findViewById(R.id.map_spinner_store);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.simple_spinner_item, spinnerItems);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    storeSpinner.setAdapter(spinnerArrayAdapter);

                    ArrayList<HashMap<String, String>> stores = getCategoryAllStoresInfo(position);
                    updateMapMarker(stores);
                }


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
                if (currentCategory == 0) {
                    ArrayList<HashMap<String, String>> stores = getOneStoresInfoOfAll(position);
                    updateMapMarker(stores);
                } else {
                    ArrayList<HashMap<String, String>> stores = getOneStoresInfo(currentCategory - 1, position);
                    updateMapMarker(stores);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private ArrayList<String> getAllStoresList() {
        ArrayList<String> sList = new ArrayList<>();
        sList.add("All");
        for (int i=0; i < storeResIds.size();i++){
//        for (LinkedHashMap<String, Integer> category: storeResIds) {
            String categoryName = categoryArray[i+1];
            LinkedHashMap<String, Integer> category = storeResIds.get(i);
            for (String storename : category.keySet()) {
                sList.add(categoryName + " - " + storename);
            }

        }
        return sList;
    }

    private ArrayList<HashMap<String,String>> getAllStoresInfo() {
        ArrayList<Integer> sList = new ArrayList<>();
        for (LinkedHashMap<String, Integer> category: storeResIds) {
            for (Integer storeResID : category.values()) {
                sList.add(storeResID);
            }
        }

        ArrayList<HashMap<String, String>> stores = new ArrayList<>();
        for (Integer storesResID:sList){
            float color = getStoreMarkColor(storesResID);

            for (String storeInfo:getResources().getStringArray(storesResID)){
                stores.add(formateStoreInfo(storeInfo, color));
            }
        }
        return stores;
    }

    private ArrayList<String> getStoresListOfCategory(int position) {


        ArrayList<String> sList = new ArrayList<>();
        sList.add("All");
        Log.d(TAG, " keyset:" + storeResIds.get(position).keySet());
        for (String storename:storeResIds.get(position).keySet()){
            sList.add(storename);
        }
        return sList;
    }

    private ArrayList<HashMap<String,String>> getOneStoresInfoOfAll(int position) {
        ArrayList<Integer> sList = new ArrayList<>();
        for (LinkedHashMap<String, Integer> category: storeResIds) {
            for (Integer storeResID : category.values()) {
                sList.add(storeResID);
            }
        }


        ArrayList<Integer> requestResIds = new ArrayList<>();
        if (position == 0) {
            requestResIds = sList;
        }
        else {
            requestResIds.add(sList.get(position-1));
        }

        ArrayList<HashMap<String, String>> stores = new ArrayList<>();
        for (Integer storesResID:requestResIds){
            float color = getStoreMarkColor(storesResID);

            for (String storeInfo:getResources().getStringArray(storesResID)){
                stores.add(formateStoreInfo(storeInfo, color));
            }
        }
        return stores;
    }

    private ArrayList<HashMap<String,String>> getOneStoresInfo(int currentCategory, int position) {

        if (position == 0){
            return getCategoryAllStoresInfo(currentCategory);
        }

        position = position-1;
        Integer storesResID = (new ArrayList<Integer>(storeResIds.get(currentCategory).values())).get(position);
        float color = getStoreMarkColor(storesResID);

        ArrayList<HashMap<String, String>> stores = new ArrayList<>();
        for (String storeInfo:getResources().getStringArray(storesResID)){
            stores.add(formateStoreInfo(storeInfo, color));
        }


        return stores;
    }

    private ArrayList<HashMap<String, String>> getCategoryAllStoresInfo(int position) {

        ArrayList<HashMap<String, String>> stores = new ArrayList<HashMap<String, String>>();
        for (int storesResID: storeResIds.get(position).values()){
            float color = getStoreMarkColor(storesResID);

            for (String storeInfo:getResources().getStringArray(storesResID)) {
                stores.add(formateStoreInfo(storeInfo, color));
            }

        }
        return stores;
    }

    private HashMap<String, String> formateStoreInfo(String storeInfo, Float color){
        Log.d(TAG, "storeInfo:" +storeInfo);
        String[] sInfo = storeInfo.split("@");
        String[] latlng = sInfo[0].split(",");
        HashMap<String, String> store = new HashMap<>();
        store.put("name", sInfo[1]);
        store.put("addr", sInfo[2]);
        store.put("lat", latlng[0]);
        store.put("lng", latlng[1]);
        store.put("color", String.valueOf(color));
        return store;
    }

    private float getStoreMarkColor(Integer storesResID){
        for (Integer id: foodStoresResID){
            if (id.equals(storesResID)){
                return BitmapDescriptorFactory.HUE_ROSE;
            }
        }
        for (Integer id: clothStoresResID){
            if (id.equals(storesResID)){
                return BitmapDescriptorFactory.HUE_VIOLET;
            }
        }


        return BitmapDescriptorFactory.HUE_RED;
    }


    private void updateMapMarker(ArrayList<HashMap<String, String>> stores) {
        selectedStores = stores;

        mMap.clear();
        for (HashMap<String,String> s:stores){
            String name = s.get("name");
            String addr = s.get("addr");
            Double lat = Double.valueOf(s.get("lat"));
            Double lng = Double.valueOf(s.get("lng"));
            LatLng storeLatlng = new LatLng(lat,lng);
            Float color = Float.valueOf(s.get("color"));


            mMap.addMarker(new MarkerOptions().position(storeLatlng).title(name).snippet(addr)
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));

            Log.d(TAG, "mMap+:" + s.toString());
        }
    }

    private void updateMapMarker() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        mMap.clear();
        for (HashMap<String,String> s:selectedStores){
            String name = s.get("name");
            String addr = s.get("addr");
            Double lat = Double.valueOf(s.get("lat"));
            Double lng = Double.valueOf(s.get("lng"));
            LatLng storeLatlng = new LatLng(lat,lng);
            Float color = Float.valueOf(s.get("color"));

            if (bounds.contains(storeLatlng)) {
                mMap.addMarker(new MarkerOptions().position(storeLatlng).title(name).snippet(addr)
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
                Log.d(TAG, "mMap+:" + s.toString());
            }

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
        final LatLng tokeyTower = new LatLng(35.658554, 139.745572);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tokeyTower, 13));
        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                // Check if there is an open info window
                if (lastOpenned != null) {
                    // Close the info window
                    lastOpenned.hideInfoWindow();

                    // Is the marker the same marker that was already open
                    if (lastOpenned.equals(marker)) {
                        // Nullify the lastOpenned object
                        lastOpenned = null;
                        // Return so that the info window isn't openned again
                        return true;
                    }
                }

                // Open the info window for the marker
                marker.showInfoWindow();
                // Re-assign the last openned such that we can close it later
                lastOpenned = marker;

                // Event was handled by our code do not launch default behaviour.
                return true;
            }
        });

//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            @Override
//            public void onCameraChange(CameraPosition position) {
//                updateMapMarker();
//                //fetchData(bounds);
//            }
//        });

    }


}
