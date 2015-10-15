package com.example.brandon.jpbestbuy;

import java.util.HashMap;

/**
 * Created by ty on 2015/10/8.
 */
public class Product {

    public int id;
    public String name;
    public int amount;
    public boolean buy;
    public HashMap<Integer,Integer> storePrice; //<SID, Price>

    public Product(HashMap<String, Object> pInfo, HashMap<Integer,Integer> storePrice){
        this.id = (Integer) pInfo.get("_id");
        this.name = (String) pInfo.get("Name");
        this.amount = (Integer) pInfo.get("AMOUNT");
        this.storePrice = storePrice;
    }

    public Product(int id, String name, int amount){
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.storePrice = new HashMap<>();
    }

    public Product(Product p){
        this.id = p.id;
        this.name = p.name;
        this.amount = p.amount;
        this.storePrice = new HashMap<>(p.storePrice);
    }

    public String toString(){
        return "[pid:"+id+",name:"+name+",amount:"+amount+",buy:"+String.valueOf(buy)+",StorePrice:"+storePrice+"]";
    }

    public void removeStorePrice(int sid){
        storePrice.remove(sid);
    }

    public void addStorePrice(HashMap<Integer,Integer> storePrice){
        this.storePrice = storePrice;
    }

    public Integer getCheapestStore(){
        Integer minPrice = null;
        Integer minSid = null;
        for (Integer sid : storePrice.keySet()){
            Integer price = storePrice.get(sid);
            if (minPrice==null){
                minPrice = price;
                minSid = sid;
            } else if (price < minPrice){
                minPrice = price;
                minSid = sid;
            }
        }
        return minSid;
    }

    public Integer[] getCheapestStorePrice(){
        Integer minPrice = null;
        Integer minSid = null;
        for (Integer sid : storePrice.keySet()){
            Integer price = storePrice.get(sid);
            if (minPrice==null){
                minPrice = price;
                minSid = sid;
            } else if (price < minPrice){
                minPrice = price;
                minSid = sid;
            }
        }
        Integer[] result = {minSid, minPrice};
        return result;
    }

}
