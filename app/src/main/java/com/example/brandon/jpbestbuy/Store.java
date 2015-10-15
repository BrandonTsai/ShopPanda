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
        HashMap<String,Object> product = products.get(pid);
        product.put("BUY", false);
        products.put(pid,product);
        Log.d(TAG, "Store " + sid + "not buy product:" + product.toString());
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


}
