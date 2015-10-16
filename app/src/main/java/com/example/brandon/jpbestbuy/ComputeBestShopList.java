package com.example.brandon.jpbestbuy;

import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by ty on 2015/10/8.
 */
public class ComputeBestShopList {

    private static final String TAG="ComputeBestShopList";

    public ComputeBestShopList(){

    }


    public HashMap<Integer, Store> getBestShopList(ArrayList<String> storeIds){

        HashMap<Integer, Store> stores = getStoresFromIds(storeIds);
        HashMap<Integer, Product> products = getProducts(storeIds);
        Log.d(TAG,"Stores:"+stores.toString());
        Log.d(TAG,"Products:"+products.toString());


        HashMap<Integer, Store> minCostStores = getMinCostStores(stores, products);
        int minCost = countTotalBuyCost(minCostStores);
        Log.d(TAG,"minCost="+minCost+"\n");
        Log.d(TAG,"\n---------------\n");


        minCostStores = buyProductInThresholdStores(minCostStores, products);
        minCost = countTotalBuyCost(minCostStores);
        Log.d(TAG, "adjust minCost=" + minCost);

        HashMap<Integer, Store> notThresholdStores = getNotThresholdStores(minCostStores);

//        while (notThresholdStores.size()>1 && hasStoreCanReachThreshold(notThresholdStores)){
        if (notThresholdStores.size()>1 && hasStoreCanReachThreshold(notThresholdStores)){
            Log.d(TAG,"Compute best shop for notThresholdStores");
            //HashMap<Integer, Product> notThresholdProducts = getNotThresholdProducts(minCostStores, products);
            int minCostNTStoreID = -1;
            int minTotalCost = 0;
            for (Store store : notThresholdStores.values()){
                HashMap<Integer, Store> tmpNTStores = letStoreReachthreshold(store.sid, notThresholdStores);
            }

        }

        HashMap<Integer, Store> bestShopList = new HashMap<>();
        return bestShopList;
    }

    private HashMap<Integer, Store> letStoreReachthreshold(int sid, HashMap<Integer, Store> stores) {
        Store store = stores.get(sid);
        store.getMinProductSetToReachThreshold();
        return null;
    }

    private boolean hasStoreCanReachThreshold(HashMap<Integer, Store> notThresholdStores) {
        for (Store s : notThresholdStores.values()){
            if (s.canReachThreshold()) {
                return true;
            }
        }

        return false;
    }


    private HashMap<Integer, Store> getStoresFromIds(ArrayList<String> storeIds) {

        HashMap<Integer, Store> selectStores = new HashMap<>();

        ArrayList<HashMap<String, String>> storeArrayList = DB.getStoreArrayList();
        for (HashMap<String, String> storeHashMap: storeArrayList) {
            if (storeIds.contains(storeHashMap.get("_id"))) {
                Store store = new Store(storeHashMap);
                selectStores.put(store.sid, store);
            }
        }
        return selectStores;
    }


    public HashMap<Integer, Product> getProducts(ArrayList<String> storeIds){
        HashMap<Integer, Product> products = new HashMap<>();
        Cursor cursor = DB.getAllProduct();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Product p = new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
            HashMap<Integer, Integer> storePrice = new HashMap<>();
            HashMap<Integer, Integer> allStorePrice = DB.getStorePriceByPID(p.id);
            for (Integer sid: allStorePrice.keySet()){
                if (storeIds.contains(String.valueOf(sid))){
                    storePrice.put(sid, allStorePrice.get(sid));
                }
            }
            p.addStorePrice(storePrice);
            products.put(p.id, p);
            cursor.moveToNext();
        }
        cursor.moveToFirst();
        return products;
    }


    public HashMap<Integer, Store> getMinCostStores(HashMap<Integer, Store> stores, HashMap<Integer, Product> products){
        HashMap<Integer, Store> minCostStores = cloneStoreList(stores);
        // buy each product with cheapest prices.
        for (Product product: products.values()){
            Integer sid = product.getCheapestStore();
            minCostStores.get(sid).setBuyProduct(product.id);
        }
        Log.d(TAG, "minCostStores:" + minCostStores.toString());
        return minCostStores;
    }


    public HashMap<Integer, Store> cloneStoreList(HashMap<Integer, Store> stores){
        HashMap<Integer, Store> newStores = new HashMap<>();
        for (Integer key:stores.keySet()){
            newStores.put(key, stores.get(key).clone());
        }
        return newStores;
    }

    public int countTotalBuyCost(HashMap<Integer, Store> stores){
        int totalCost = 0;
        for (Store store: stores.values()){
            totalCost += store.getBuyCostWithDiscount();
        }
        return totalCost;
    }


    private HashMap<Integer, Store> buyProductInThresholdStores(HashMap<Integer, Store> stores, HashMap<Integer, Product> products) {
        Log.d(TAG, "buyProductInThresholdStores?");
        for (Store store : stores.values()){
            if (store.reachThreshold()){
                // if noBuyProduct's price *discount is cheaper than other stores
                ArrayList<Integer> notBuyProductsID = store.getNoBuyProductsID();
                if (notBuyProductsID.size()>0){
                    for (int pid : notBuyProductsID){
                        Integer[] minStorePrice = products.get(pid).getCheapestStorePrice();
                        int discountPrice = store.getProductDiscountPrice(pid);
                        if (discountPrice <= minStorePrice[1]){
                            store.setBuyProduct(pid);
                            stores.get(minStorePrice[0]).setNotBuyProduct(pid);
                        }
                    }
                }
            }
        }
        return stores;
    }

    private TreeSet<Integer> getThresholdStoresProductsIDSet(HashMap<Integer, Store> stores){
        TreeSet<Integer> thresholdStoreProducts = new TreeSet<>();
        for (Store store: stores.values()){
            if (store.reachThreshold()) {
                for (int pid : store.getBuyProductsID()) {
                    thresholdStoreProducts.add(pid);
                }
            }
        }
        Log.d(TAG, "getThresholdStoresProductsIDSet:" + thresholdStoreProducts.toString());
        return thresholdStoreProducts;

    }

    private TreeSet<Integer> getThresholdStoresStoreIDSet(HashMap<Integer, Store> stores) {
        TreeSet<Integer> thresholdStores = new TreeSet<>();
        for (Store store: stores.values()){
            if (store.reachThreshold()) {
                thresholdStores.add(store.sid);
            }
        }
        Log.d(TAG, "getThresholdStoresIDSet:" + thresholdStores.toString());
        return thresholdStores;
    }


    private HashMap<Integer, Store> getNotThresholdStores(HashMap<Integer, Store> stores){
        TreeSet<Integer> notBuyProductsIDSet = getThresholdStoresProductsIDSet(stores);
        HashMap<Integer, Store> notThresholdStores = new HashMap<>();
        for (Store store: stores.values()) {
            if (!store.reachThreshold()) {
                Store s = store.clone();
                Set<Integer> pids =  store.products.keySet();
                for (int pid: pids){
                    if (notBuyProductsIDSet.contains(pid)){
                        s.products.remove(pid);
                    }
                }
                notThresholdStores.put(s.sid, s);
            }
        }

        Log.d(TAG, "notThresholdStores:" + notThresholdStores.toString());
        return notThresholdStores;
    }

    private HashMap<Integer,Product> getNotThresholdProducts(HashMap<Integer, Store> stores, HashMap<Integer, Product> products) {
        Log.d(TAG, "getNotThresholdProducts?");
        TreeSet<Integer> notBuyProductsIDSet = getThresholdStoresProductsIDSet(stores);
        TreeSet<Integer> notBuyStoreIDSet = getThresholdStoresStoreIDSet(stores);

        HashMap<Integer, Product> notThresholdProducts = new HashMap<>();
        for (Product product: products.values()){
            if (!notBuyProductsIDSet.contains(product.id)){
                Product p = new Product(product);
                for (Integer sid: notBuyStoreIDSet){
                    if (p.storePrice.keySet().contains(sid)){
                        p.removeStorePrice(sid);
                    }
                }
                notThresholdProducts.put(p.id, p);
            }
        }
        Log.d(TAG, "notThresholdProducts:" + notThresholdProducts.toString());
        return notThresholdProducts;

    }



}
