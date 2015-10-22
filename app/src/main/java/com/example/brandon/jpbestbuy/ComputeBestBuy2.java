package com.example.brandon.jpbestbuy;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


public class ComputeBestBuy2 extends AppCompatActivity {

	private static String TAG = Utils.TAG + "(Compute)";
	HashMap<Integer, Integer> amountMap = new HashMap<>();
	HashMap<Integer, String> productNameMap = new HashMap<>();
	HashMap<Integer, Integer[]> storeInfoMap = new HashMap<>();
	HashMap<Integer, String> storeNameMap = new HashMap<>();

	HashMap<Integer, ArrayList<Integer[]>> buyList = new HashMap<>();
	HashMap<Integer, ArrayList<Integer[]>> noBuyList = new HashMap<>();


	TreeSet<Integer> haveDiscount = new TreeSet<Integer>();
	TreeSet<Integer> noDiscount = new TreeSet<Integer>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compute_best_buy2);
		Log.d(TAG, "start Compute Best Buy");

		Bundle bundle = this.getIntent().getExtras();
		ArrayList<String> storeIds = (ArrayList<String>) bundle.get("storeIds");
        Log.d(TAG, "selected Stores:" +storeIds.toString());
		getAmount();
		getStoreInfo(storeIds);
		computeBestResult();
		showResult();

	}

	private void showResult() {

		HashMap<Integer, Integer> eachStoreCost = getEachStoreCost(buyList);
		Integer totalCost = getTotalCost(eachStoreCost);

		TextView bestCost = (TextView) findViewById(R.id.tv_best_cost);
		bestCost.setText("Total Cost: " + String.valueOf(totalCost) + " (JPD)");

		ExpandableListView elv = (ExpandableListView)findViewById(R.id.expListView_BestBuyList);

		List<Map<String, String>> groups = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> childs = new ArrayList<List<Map<String, String>>>();

		for (int sid : buyList.keySet()){
			String storeName = storeNameMap.get(sid);
			Map<String, String> group = new HashMap<String, String>();
			Integer[] storeInfo = storeInfoMap.get(sid);
			Integer threshold = storeInfo[0];
			Integer discount = storeInfo[1];
			Integer cost = eachStoreCost.get(sid);
			if (cost >= threshold){
				double d = (1.00 - discount*0.01);
				cost = (int) Math.ceil(cost * (1.00 - discount*0.01));
			}
			group.put("group", storeName + " (Total Cost: " + String.valueOf(cost) + ")");
			groups.add(group);

			List<Map<String, String>> child = new ArrayList<Map<String, String>>();
			ArrayList<Integer[]> buyListItems = buyList.get(sid);
			for (int i = 0 ; i < buyListItems.size(); i++){
				Map<String, String> child1Data = new HashMap<String, String>();
				Integer[] a = buyListItems.get(i);
				//Log.d(sCursor, ">>" + Arrays.toString(a));
				String amount = String.valueOf(a[1]);
				String price = String.valueOf(a[2]);
				String name =productNameMap.get(a[0]);
				String pInfo = name + System.getProperty("line.separator") + " (amount:" + amount +
						", price:" + price +
						" JPD/each, total cost:" + String.valueOf((a[1] * a[2])) +
						")";
				child1Data.put("child", pInfo);
				child.add(child1Data);
			}
			childs.add(child);
		}

		SimpleExpandableListAdapter viewAdapter = new SimpleExpandableListAdapter(
				this,
				groups,
				R.layout.expandable_list_group_item,
				new String[] { "group" },
				new int[] { R.id.groupText },
				childs,
				android.R.layout.simple_expandable_list_item_2,
				new String[] { "child" },
				new int[] { android.R.id.text1 }

		);
		elv.setAdapter(viewAdapter);
		for ( int i = 0; i < groups.size(); i++ ) {
			elv.expandGroup(i);
		}

	}

	private void getStoreInfo(ArrayList<String> storeIds) {
		Cursor cursor = DB.getStore();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false ){
			String sid = String.valueOf(cursor.getInt(0));
			if (storeIds.contains(sid)) {
				Integer[] info = new Integer[2];
				info[0] = cursor.getInt(2); // threshold
				info[1] = cursor.getInt(3); // discount
				storeInfoMap.put(cursor.getInt(0), info);
				storeNameMap.put(cursor.getInt(0), cursor.getString(1));
			}
			cursor.moveToNext();
		}
		cursor.close();
		Log.d(TAG, ">> StoreInfo:"+storeInfoMap.toString());
	}

	private void getAmount() {
		Cursor cursor = DB.getAllProduct();
		Log.d(TAG, Utils.cur2Json(cursor).toString());
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false){
			Integer pid = cursor.getInt(0);
			Integer amount = cursor.getInt(2);
			amountMap.put(pid, amount);
			productNameMap.put(pid, cursor.getString(1));
			cursor.moveToNext();
		}
		cursor.close();
		//Log.d(sCursor, "Amount:" + amount.toString());
	}


	private HashMap<Integer, ArrayList<Integer[]>> cloneList(TreeSet<Integer> keys, HashMap<Integer, ArrayList<Integer[]>> oriList){
		HashMap<Integer, ArrayList<Integer[]>> newList = new HashMap<>();

		for (int k : keys){
			ArrayList<Integer[]> newItemList = new ArrayList<Integer[]>();
			for (Integer[] item: oriList.get(k)){
				Integer[] newItem = Arrays.copyOf(item, item.length);
				newItemList.add(newItem);
			}
			newList.put(k, newItemList);
		}
		return newList;
	}


	private void computeBestResult() {
		/***
		 * HashMap<Integer, ArrayList<Integer[]>> buyList  = {sid : ProductList}
		 * HashMap<Integer, ArrayList<Integer[]>> noBuyList = {sid : ProductList}
		 *
		 * HashMap<Integer, ArrayList<Integer[]>> must_buy_store_product = {sid : ProductList}
		 */


		/***
		 * select cheapest product for each store
		 ***/
		Cursor c = DB.getPriceOrderByPID();
		Log.d(TAG, "Order:" + Utils.cur2Json(c).toString());
		getCheapest(c);



		/***
		 * count current total cost = getTotalCost(all_store, buyList)
		 */
		HashMap<Integer, Integer> eachStoreCost = getEachStoreCost(buyList);
		Integer oriTotalCost = getTotalCost(eachStoreCost);
		Log.d(TAG, "totalCost=" + oriTotalCost);


		printList(buyList);

		while (noDiscount.size() >= 2 && existOneStoreCanGetDiscount(noDiscount)){
			Log.d(TAG, "---------------------Round-----------------------------");
			//count origin Total cost of all no Discount stores
			HashMap<Integer, ArrayList<Integer[]>> oriNoDiscountBuyList = new HashMap<>();
			HashMap<Integer, ArrayList<Integer[]>> oriNoDiscountNoBuyList = new HashMap<>();
			/*
			for ( int sid: noDiscount){
				oriNoDiscountBuyList.put(sid, buyList.get(sid));
				oriNoDiscountNoBuyList.put(sid, noBuyList.get(sid));
			}
			*/

			oriNoDiscountBuyList = cloneList(noDiscount, buyList);
			oriNoDiscountNoBuyList = cloneList(noDiscount, noBuyList);
			printList(oriNoDiscountBuyList);
			printList(oriNoDiscountNoBuyList);

			HashMap<Integer, Integer> oriEachStoreCost = getEachStoreCost(oriNoDiscountBuyList);
			int oriNoDiscountTotalCost = getTotalCost(oriEachStoreCost);
			Log.d(TAG, "oriNoDiscountTotalCost=" + oriNoDiscountTotalCost);



			// Let One store get discount
			int sid = selectCloestThresholdStore(oriEachStoreCost);
			Log.d(TAG, "select store " +sid);
			//if (canGetThreshold(sid, oriEachStoreCost.get(sid))){}
			int currentCost = oriEachStoreCost.get(sid);
			int threshold = storeInfoMap.get(sid)[0];
			while (currentCost < threshold) {
				Integer[] selectProduct = selectOneProduct(sid, threshold - currentCost, oriNoDiscountNoBuyList.get(sid), oriNoDiscountBuyList);
				//remove selectPid in Other Store.
				for (int s : oriNoDiscountBuyList.keySet()){
					Integer[] pObj = getProductObjectbyPid(oriNoDiscountBuyList.get(s), selectProduct[0]);
					if (pObj != null){
						oriNoDiscountBuyList.get(s).remove(pObj);
						Log.d(TAG, s + " remove product " + Arrays.toString(pObj));
					}
				}
				oriNoDiscountBuyList.get(sid).add(selectProduct);
				oriNoDiscountNoBuyList.get(sid).remove(selectProduct);
				Log.d(TAG, "currentCost:" + currentCost + ", select Product:" + Arrays.toString(selectProduct));
				currentCost += (selectProduct[2]*selectProduct[1]);
				Log.d(TAG,"currentCost:" + currentCost + ", threshold:"+threshold);
			}



			int newNoDiscountTotalCost = getTotalCost(getEachStoreCost(oriNoDiscountBuyList));
			Log.d(TAG, "newNoDiscountTotalCost" +newNoDiscountTotalCost);

			if ( newNoDiscountTotalCost < oriNoDiscountTotalCost){
				Log.d(TAG, newNoDiscountTotalCost + "<" + oriNoDiscountTotalCost);
				for (int s : noDiscount) {
					buyList.put(s, oriNoDiscountBuyList.get(s));
					noBuyList.put(s, oriNoDiscountNoBuyList.get(s));
				}
				noDiscount.remove(sid);
				haveDiscount.add(sid);
			}else {
				break;
			}

		}

		printList(buyList);

	}

	private void printList(HashMap<Integer, ArrayList<Integer[]>> myList){
		Log.d(TAG, "~~~~~" );
		for (int s : myList.keySet()){
			Log.d(TAG, "stroe " + s + ":");
			for (Integer[] p: myList.get(s)){
				Log.d(TAG, ">" + Arrays.toString(p));
			}
		}
		Log.d(TAG, "~~~~~");
	}

	public Integer[] getProductObjectbyPid(ArrayList<Integer[]> pList, Integer pid) {
		Integer[] pObj = null;
		for (Integer[] p : pList ){
			if (p[0].equals(pid)){
				return p;
			}
		}

		return pObj;
	}

	private boolean existOneStoreCanGetDiscount(TreeSet<Integer> noDiscount) {
		HashMap<Integer, ArrayList<Integer[]>> oriNoDiscountBuyList = new HashMap<>();
		HashMap<Integer, ArrayList<Integer[]>> oriNoDiscountNoBuyList = new HashMap<>();
		for ( int sid: noDiscount){
			oriNoDiscountBuyList.put(sid, buyList.get(sid));
			oriNoDiscountNoBuyList.put(sid, noBuyList.get(sid));
		}
		HashMap<Integer, Integer> oriEachStoreCost = getEachStoreCost(oriNoDiscountBuyList);

		for (int sid:noDiscount){
			if (canGetThreshold(sid, oriEachStoreCost.get(sid))){
				return true;
			}
		}
		return false;
	}

	private int selectCloestThresholdStore(HashMap<Integer, Integer> eachStoreCost) {
		int select = 0;
		int minDiff = 999999;
		for (int sid : eachStoreCost.keySet()){
			int diff = storeInfoMap.get(sid)[0] - eachStoreCost.get(sid);
			if (diff < minDiff && canGetThreshold(sid, eachStoreCost.get(sid))){
				minDiff = diff;
				select = sid;
			}
		}
		return select;
	}

	private boolean canGetThreshold(int sid, int currentCost) {
		if(noBuyList.get(sid) == null){
			return false;
		}

		int noBuyCost = 0;
		for (Integer[] pInfo : noBuyList.get(sid)){
			noBuyCost += (pInfo[1]*pInfo[2]);
		}

		if ( (noBuyCost + currentCost) >= storeInfoMap.get(sid)[0]){
			return true;
		}

		return false;
	}

	public Integer[] selectOneProduct(Integer sid, int requireCost, ArrayList<Integer[]> pList, HashMap<Integer, ArrayList<Integer[]>> noDiscountBuyList) {
		//selectOneProduct(sid, threshold - currentCost, oriNoDiscountNoBuyList.get(sid), oriNoDiscountBuyList);

		Integer[] minAboveProduct = null; //[pid, diff]
		int minAboveCost = 99999;
		Integer[] maxLowerProduct = null; //[pid, diff]
		int maxLowerCost = -99999;

		for (Integer[] p : pList){
			if (notInOtherNoDiscountStore(p, noDiscountBuyList, sid)){
				Log.d(TAG, p[0] + " notInOtherNoDiscountStore");
				continue;
			}
			int diff = (p[1]*p[2])-requireCost;
			if (diff >= 0){
				if (minAboveProduct == null || diff < minAboveCost){
					minAboveProduct = p;
					minAboveCost = diff;
				}
			} else {
				if (maxLowerProduct == null || diff > maxLowerCost){
					maxLowerProduct = p;
					maxLowerCost = diff;
				}
			}
		}

		if (minAboveProduct != null){
			Log.d(TAG, "select product:" + minAboveProduct[0]);
			return minAboveProduct;
		}else if (maxLowerProduct != null ){
			Log.d(TAG, "select product:" + maxLowerProduct[0]);
			return maxLowerProduct;
		}
		Log.d(TAG, "select one product is null??");
		return null;

	}

	private boolean notInOtherNoDiscountStore(Integer[] selectedProduct, HashMap<Integer, ArrayList<Integer[]>> noDiscountBuyList, Integer except_sid ) {

		for (Integer sid:noDiscountBuyList.keySet()){
			if (!sid.equals(except_sid)) {
				for (Integer[] p : noDiscountBuyList.get(sid)) {
					if (p[0].equals(selectedProduct[0])) {
						return false;
					}
				}
			}
		}
		return true;
	}


	private HashMap<Integer, Integer> getEachStoreCost(HashMap<Integer, ArrayList<Integer[]>> buyList ){
		HashMap<Integer, Integer> eachStoreCost = new HashMap<>();

		for(int sid:buyList.keySet()){
			//Log.d(sCursor, "Store : " + sid);
			Integer storeCost = 0;
			ArrayList<Integer[]> buyListItems = buyList.get(sid);
			for (int i = 0 ; i < buyListItems.size(); i++){
				Integer[] a = buyListItems.get(i);
				//Log.d(sCursor, ">>" + Arrays.toString(a));
				Integer amount = a[1];
				Integer price = a[2];
				storeCost = storeCost + (price * amount);
			}
			eachStoreCost.put(sid, storeCost);
		}

		return eachStoreCost;
	}


	private Integer getTotalCost(HashMap<Integer, Integer> eachStoreCost ){
		Integer totalCost = 0;
		for (int i:eachStoreCost.keySet()){
			Integer cost = eachStoreCost.get(i);
			Integer[] storeInfo = storeInfoMap.get(i);
			Integer threshold = storeInfo[0];
			Integer discount = storeInfo[1];
			if (cost >= threshold ){
				haveDiscount.add(i);

				double d = (1.00 - discount*0.01);
				Log.d(TAG, i + "(" + cost + ") reach threshold:" +threshold +",d=" + String.valueOf(d) );
				//Log.d(sCursor, "d=" + String.valueOf(d));
				cost = (int) Math.ceil(cost * (1.00 - discount*0.01));
			}
			else{
				noDiscount.add(i);
			}
			Log.d(TAG,"store " + i + " cost:" + cost);
			totalCost += cost;
		}
		return totalCost;
	}


	private void getCheapest(Cursor cursor){
		cursor.moveToFirst();
		Integer previousPid = -1;
		while (cursor.isAfterLast() == false) {
			Integer pid = cursor.getInt(2);
			Integer sid = cursor.getInt(1);
			Integer price = cursor.getInt(3);
			Integer amount = amountMap.get(pid);
			Integer[] lower = { pid, amount, price};
			if (pid.equals(previousPid)){
				//Log.d(sCursor, "higher! [sid,pid,amount,price] = " + Arrays.toString(lower));
				if (noBuyList.containsKey(sid)){
					ArrayList<Integer[]> buyListItem = noBuyList.get(sid);
					buyListItem.add(lower);
				} else {
					ArrayList<Integer[]> buyListItem = new ArrayList<Integer[]>();
					buyListItem.add(lower);
					noBuyList.put(sid, buyListItem);
				}
			}
			else {
				//Log.d(sCursor, "lower! [sid,pid,amount,price] = " + Arrays.toString(lower));
				previousPid = pid;
				if (buyList.containsKey(sid)){
					ArrayList<Integer[]> buyListItem = buyList.get(sid);
					buyListItem.add(lower);
				} else {
					ArrayList<Integer[]> buyListItem = new ArrayList<Integer[]>();
					buyListItem.add(lower);
					buyList.put(sid, buyListItem);
				}
			}
			cursor.moveToNext();
		}
		cursor.close();

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_compute_best_buy2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
