package com.example.brandon.jpbestbuy;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ty on 2015/10/8.
 */
public class Store {

    private static final String TAG="Store";

    public int sid;
    public String storeName;
    public int threshould;
    public int discount;
    public String location;

    // [pid -> <pid, productName, amount, price, buy>]
    public HashMap<Integer, HashMap<String,Object>> products;

    public Store(HashMap<String, String> storeInfo){
        this.sid = Integer.valueOf(storeInfo.get("_id"));
        this.storeName = storeInfo.get("NAME");
        this.threshould = Integer.valueOf(storeInfo.get("THRESHOLD"));
        this.discount = Integer.valueOf(storeInfo.get("DISCOUNT"));
        this.location = storeInfo.get("LOCATION");
        this.products = getProductsInfoAndPrice();
    }

    public Store(int sid, String storeName, int threshould , int discount, String location, HashMap<Integer, HashMap<String,Object>> products){
        this.sid = sid;
        this.storeName = storeName;
        this.threshould = threshould;
        this.discount = discount;
        this.location = location;
        this.products = new HashMap<>(products);
    }

    public String toString(){
        return "[sid:"+sid+",name:"+storeName+",threshold:"+threshould+",discount:"+discount+",products:"+products.toString()+"]\n";
    }

    public Store clone(){
        Store newStore = new Store(sid, storeName, threshould, discount,location, products);
        return newStore;
    }

    public int getTotalCost(){
        int totalCost = 0;
        for (HashMap<String,Object> pInfo:products.values()){
            int price = (Integer) pInfo.get("PRICE");
            int amount = (Integer) pInfo.get("AMOUNT");
            totalCost += (price * amount);
        }
        return totalCost;
    }

    public int getBuyCost(){
        int totalCost = 0;
        for (HashMap<String,Object> pInfo:products.values()){
            if ((Boolean) pInfo.get("BUY")) {
                int price = (Integer) pInfo.get("PRICE");
                int amount = (Integer) pInfo.get("AMOUNT");
                totalCost += (price * amount);
//                Log.d(TAG, "Store " + sid + " buy:" + pInfo.toString());
            }
        }
        Log.d(TAG, "Store "+sid+ " totalShopCost=" + totalCost);
        return totalCost;
    }

    public int getBuyCostWithDiscount(){
        int totalCost = getBuyCost();

        if (totalCost >= threshould ){
            totalCost = (int) Math.ceil(totalCost * (1.00 - discount*0.01));
            Log.d(TAG, "Store "+sid+ " totalShopCost reach " + threshould +"JPD, get discount:" + discount + "%, new totalShopCost=" + totalCost);
        }

        return totalCost;
    }

    public void setBuyProduct(int pid){
        HashMap<String,Object> product = products.get(pid);
        product.put("BUY", true);
        products.put(pid,product);
        Log.d(TAG, "Store " +sid + " buy product:" + product.toString());
    }

    public void setNotBuyProduct(int pid){
        if (products.containsKey(pid)) {
            HashMap<String, Object> product = products.get(pid);
            product.put("BUY", false);
            products.put(pid, product);
            Log.d(TAG, "Store " + sid + "not buy product:" + product.toString());
        }
    }

    public boolean reachThreshold(){
        if (getBuyCost() >= threshould){
            return true;
        }
        return false;
    }

    public boolean canReachThreshold(){
        if (getTotalCost() >= threshould){
            return true;
        }
        return false;
    }


    public ArrayList<Integer> getBuyProductsID(){
        ArrayList<Integer> buys = new ArrayList<>();
        for (HashMap<String,Object> pInfo:products.values()){
            if ((Boolean) pInfo.get("BUY")) {
                int pid = (Integer) pInfo.get("PID");
                buys.add(pid);
            }
        }
        return buys;
    }

    public ArrayList<Integer> getNoBuyProductsID(){
        ArrayList<Integer> nobuys = new ArrayList<>();
        for (HashMap<String,Object> pInfo:products.values()){
            if (!(Boolean) pInfo.get("BUY")) {
                int pid = (Integer) pInfo.get("PID");
                nobuys.add(pid);
            }
        }
        Log.d(TAG, "Store " + sid + "does not buy product:" + nobuys.toString());
        return nobuys;
    }

    public HashMap<String,Object> getProductInfo(int pid){
        if (products.keySet().contains(pid)) {
            return products.get(pid);
        }
        return null;
    }

    public HashMap<Integer, HashMap<String,Object>> getProductsInfoAndPrice(){
        HashMap<Integer, HashMap<String,Object>> productInfo = new HashMap<>();
        for (HashMap<String, Object> pInfo: DB.getAllProuctWithPrice(sid)){
            pInfo.put("BUY", false);
            productInfo.put((Integer) pInfo.get("PID"), pInfo);
        }
        Log.d(TAG, "getProductsInfoAndPrice:"+ productInfo.toString());
        return productInfo;
    }

    public int getProductDiscountPrice(int pid){
        int price = (Integer) products.get(pid).get("PRICE");
        return (int) Math.ceil(price * (1.00 - discount*0.01));
    }

    public ArrayList<Integer> getMinProductSetToReachThreshold(){
        if (!canReachThreshold()){
            Log.d(TAG,"Store " + sid + "can not reach Threshold!");
            return null;
        }

        ArrayList<Integer> minBuyProductIDs = new ArrayList<>();

        ArrayList<HashMap<String,Object>> notbuyProducts = new ArrayList<>();
        for (HashMap<String,Object> pInfo:products.values()){
            if (!(Boolean) pInfo.get("BUY")) {
                notbuyProducts.add(pInfo);
            }
        }
        Log.d(TAG, "Store " + sid + " does not buy product:" + notbuyProducts.toString());

        int diff = threshould - getBuyCost();
        Log.d(TAG, "Store " + sid + " need " + diff + "JPD to reach " + threshould + "JPD" );

        ArrayList<ArrayList<HashMap<String,Object>>> subarrays = new ArrayList<>();
        for (HashMap<String,Object> e :notbuyProducts){
            subarrays = mergeElement(subarrays, e);
        }
        Log.d(TAG, "SubArrays:" + subarrays.toString());

        ArrayList<HashMap<String,Object>> minSubArray = getMinSubArray(subarrays, diff);
        Log.d(TAG, "Min SubArrays:" + minSubArray.toString() +";cost="+getSumOfArray(minSubArray));
        for (HashMap<String, Object> pInfo: minSubArray){
            minBuyProductIDs.add((int)pInfo.get("PID"));
        }

        return minBuyProductIDs;

    }

    private ArrayList<HashMap<String,Object>> getMinSubArray(ArrayList<ArrayList<HashMap<String,Object>>> subarrays, int threshold) {
        int minCost = Integer.MAX_VALUE;
        ArrayList<HashMap<String,Object>> minSub = new ArrayList<>();
        for (ArrayList<HashMap<String,Object>> sub : subarrays) {
            int cost = getSumOfArray(sub);
            if (cost >= threshold && cost < minCost){
                minCost=cost;
                minSub = sub;
            }
        }
        return minSub;
    }

    private int getSumOfArray(ArrayList<HashMap<String,Object>> array) {
        int sum = 0;
        for (HashMap<String,Object> pInfo: array){
            int price = (int) pInfo.get("PRICE");
            int amount = (int) pInfo.get("AMOUNT");
            sum += (price * amount);
        }
        return sum;
    }

    private ArrayList<ArrayList<HashMap<String,Object>>> mergeElement(ArrayList<ArrayList<HashMap<String,Object>>> subarrays, HashMap<String,Object> e) {
        ArrayList<ArrayList<HashMap<String,Object>>> newArrays = new ArrayList<>(subarrays);
        if (subarrays.size() > 0) {
            for (ArrayList<HashMap<String,Object>> sub : subarrays) {
                ArrayList<HashMap<String,Object>> newSub = new ArrayList<>(sub);
                newSub.add(e);
                newArrays.add(newSub);
            }
        }
        ArrayList<HashMap<String,Object>> newSub = new ArrayList();
        newSub.add(e);
        newArrays.add(newSub);
        return newArrays;

    }


    public ArrayList<HashMap<String,Object>> getBuyProductsInfo(){
        ArrayList<HashMap<String,Object>> buyProducts = new ArrayList<>();
        for (HashMap<String,Object> pInfo:products.values()){
            if ((Boolean) pInfo.get("BUY")) {
                buyProducts.add(pInfo);
            }
        }
        return buyProducts;
    }

}
