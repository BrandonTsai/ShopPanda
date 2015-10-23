package com.example.brandon.jpbestbuy;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ty on 2015/9/22.
 */

// ToDo: Use BaseAdapter or CursorAdapter!!
public class SwipeAdapter extends ArrayAdapter<String> {
    private final static String TAG = "SwipeAdapter";
    private LayoutInflater mInflater ;
    private Context context;
    private List<String> objects;
    private ArrayList<HashMap<String, String>> storeList;
    private SwipeListView mSwipeListView ;

    private Dialog addStoreDlg;
    private Dialog delStoreDlg;
    private Dialog editStoreDlg;


    public SwipeAdapter(Context context, int textViewResourceId,ArrayList<HashMap<String, String>> storeList , List<String> objects, SwipeListView mSwipeListView) {
        super(context, textViewResourceId, objects);
        this.storeList= storeList;
        this.context = context;
        this.objects = objects ;
        this.mSwipeListView = mSwipeListView ;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private ArrayList<HashMap<String, String>> getStoreData() {
        Cursor cursor = DB.getStore();
        return Utils.cur2ArrayList(cursor);
    }

    private List<String> getStoreNameList(ArrayList<HashMap<String, String>> storeList) {
        List<String> items = new ArrayList<String>();
        for (HashMap<String, String>store : storeList){
            items.add(store.get("NAME"));
        }
        Log.d(TAG, "storeNameList:" + items.toString());
        return items;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null ;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.package_row, parent, false);
            holder = new ViewHolder();
            holder.mFrontText = (TextView) convertView.findViewById(R.id.example_row_tv_title);
            holder.mBackEdit = (Button) convertView.findViewById(R.id.example_row_b_action_3);
            holder.mBackDelete = (Button) convertView.findViewById(R.id.example_row_b_action_2);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mBackDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleleStoreDialog(position);
                mSwipeListView.closeOpenedItems();
            }
        });

        holder.mBackEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editStoreDialog(position);
                notifyDataSetChanged();
            }
        });

        String item = objects.get(position);
        holder.mFrontText.setText(item);
        holder.mFrontText.setPadding(20,5,5,5);
        return convertView;
    }


    public void deleleStoreDialog(int position){
        delStoreDlg = new Dialog(this.context);
        delStoreDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        delStoreDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delStoreDlg.setContentView(R.layout.dialog_del_store);

        HashMap<String, String> store = storeList.get(position);
        TextView id = (TextView) delStoreDlg.findViewById(R.id.delStoreID);
        TextView storeName = (TextView) delStoreDlg.findViewById(R.id.delStoreName);
        TextView threshold = (TextView) delStoreDlg.findViewById(R.id.delStoreThreshold);
        TextView discount = (TextView) delStoreDlg.findViewById(R.id.delStoreDiscount);

        id.setText(store.get("_id"));
        storeName.setText(store.get("NAME"));
        threshold.setText(store.get("THRESHOLD"));
        discount.setText(store.get("DISCOUNT"));

        Button btnCancel = (Button)delStoreDlg.findViewById(R.id.btnDelStoreCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                delStoreDlg.cancel();
            }
        });

        Button btnSave = (Button)delStoreDlg.findViewById(R.id.btnDelStoreSave);
        btnSave.setOnClickListener(delStoreSaveOnClkLis);
        delStoreDlg.show();
    }

    private Button.OnClickListener delStoreSaveOnClkLis = new Button.OnClickListener() {
        public void onClick(View v) {

            TextView id = (TextView) delStoreDlg.findViewById(R.id.delStoreID);
            Integer storeID = Integer.valueOf(id.getText().toString());
            Log.d(TAG, "del store: " + storeID);
            DB.delStore(storeID);
            delStoreDlg.cancel();
            removeStoreFromList(storeID);
            notifyDataSetChanged();

        }
    };

    private void removeStoreFromList(Integer sid){
        for (int p=0; p < storeList.size(); p++ ) {
            if (Integer.valueOf(storeList.get(p).get("_id")).equals(sid)) {
                storeList.remove(p);
                objects.remove(p);
            }
        }
    }

    public void editStoreDialog(int position){
        editStoreDlg = new Dialog(this.context);
        editStoreDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        editStoreDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editStoreDlg.setContentView(R.layout.dialog_add_store);

        HashMap<String,String > storeInfo = storeList.get(position);

        TextView title = (TextView) editStoreDlg.findViewById(R.id.addStoreTitle);
        title.setText("Edit Store:"+position);


        EditText storeName = (EditText) editStoreDlg.findViewById(R.id.editStoreName);
        storeName.setText(storeInfo.get("NAME"));

        EditText threshold = (EditText) editStoreDlg.findViewById(R.id.editThreshold);
        threshold.setText(storeInfo.get("THRESHOLD"));

        EditText discount = (EditText) editStoreDlg.findViewById(R.id.editDiscount);
        discount.setText(storeInfo.get("DISCOUNT"));

        Button btnCancel = (Button) editStoreDlg.findViewById(R.id.btnAddStoreCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                editStoreDlg.cancel();
            }
        });

        Button btnSave = (Button) editStoreDlg.findViewById(R.id.btnAddStoreSave);
        btnSave.setOnClickListener(editStoreSaveOnClkLis);
        editStoreDlg.show();
    }


    private Button.OnClickListener editStoreSaveOnClkLis = new Button.OnClickListener() {
        public void onClick(View v) {
            TextView title = (TextView) editStoreDlg.findViewById(R.id.addStoreTitle);
            EditText newStoreName = (EditText) editStoreDlg.findViewById(R.id.editStoreName);
            EditText newThreshold = (EditText) editStoreDlg.findViewById(R.id.editThreshold);
            EditText newDiscount = (EditText) editStoreDlg.findViewById(R.id.editDiscount);

            String[] tmp = title.getText().toString().split(":");
            int position = Integer.valueOf(tmp[1]);
            int storeID = Integer.valueOf(storeList.get(position).get("_id"));
            String newStoreNameStr = newStoreName.getText().toString();
            Integer newThresholdInt = Integer.valueOf(newThreshold.getText().toString());
            Integer newDiscountInt = Integer.valueOf(newDiscount.getText().toString());
            Log.d(TAG, "add new store: " + newStoreNameStr + newThresholdInt + newDiscountInt);
            DB.updateStore(storeID, newStoreNameStr, newThresholdInt, newDiscountInt);

            storeList= getStoreData();
            objects.set(position, storeList.get(position).get("NAME"));
            notifyDataSetChanged();
            mSwipeListView.closeOpenedItems();
            editStoreDlg.cancel();
        }
    };



    class ViewHolder{
        TextView mFrontText ;
        Button mBackEdit,mBackDelete ;
    }
}

