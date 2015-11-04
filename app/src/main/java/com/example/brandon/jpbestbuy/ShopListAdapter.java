package com.example.brandon.jpbestbuy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Brandon on 15/6/28.
 */
public class ShopListAdapter extends CursorAdapter {

    final String TAG = "ShopList";
    final Context mContext;
    final Cursor mCursor;

    public ShopListAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
        this.mCursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.textview_shoplist_name);
        TextView tvAmount = (TextView) view.findViewById(R.id.textview_shoplist_amount);
        TextView tvNameX = (TextView) view.findViewById(R.id.textview_shoplist_nameX);
        TextView tvAmountX = (TextView) view.findViewById(R.id.textview_shoplist_amountX);

        // Extract properties from cursor
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        final String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
        final int amount = cursor.getInt(cursor.getColumnIndexOrThrow("AMOUNT"));
        final boolean bought = (cursor.getInt(cursor.getColumnIndexOrThrow("BOUGHT")) != 0);
        final String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("IMAGEPATH"));
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_shoplist_product_image);
        if (imagePath.isEmpty()){
            imageView.setRotation(0);
            imageView.setImageResource(R.drawable.no_image_available);
        }else {
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d(TAG, name+" Exif: " + orientation);
                if (orientation == 6) {
                    imageView.setRotation(90);
                } else {
                    imageView.setRotation(0);
                }
                imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Populate fields with extracted properties
        tvName.setText(name);
        tvAmount.setText(String.valueOf(amount));


        if (bought){
            RelativeLayout frontLayout = (RelativeLayout) view.findViewById(R.id.front);
            frontLayout.setBackgroundColor(Color.parseColor("#F1F4F1"));
            tvName.setTextColor(Color.LTGRAY);
            tvAmount.setTextColor(Color.LTGRAY);
            tvNameX.setTextColor(Color.LTGRAY);
            tvAmountX.setTextColor(Color.LTGRAY);
        } else {
            RelativeLayout frontLayout = (RelativeLayout) view.findViewById(R.id.front);
            frontLayout.setBackgroundColor(Color.WHITE);
            tvName.setTextColor(Color.parseColor("#ff8800"));
            tvAmount.setTextColor(Color.BLUE);
            tvNameX.setTextColor(Color.BLACK);
            tvAmountX.setTextColor(Color.BLACK);
        }

        Button delBtn = (Button) view.findViewById(R.id.btn_shoplist_del);
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delProductDialog(id);
            }
        });

        Button boughtBtn = (Button) view.findViewById(R.id.btn_shoplist_bought);
        boughtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Product is bought!" + id);
                DB.setProductIsBought(id);
                updateShopList();
            }
        });


        Button editBtn = (Button) view.findViewById(R.id.btn_shoplist_edit);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Edit product!" + id);
                editProductDialog(id, name, amount, bought);
            }
        });


    }

    public void delProductDialog(final int pid){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Product is deleteed!" + pid);
                        DB.delProduct(pid);
                        updateShopList();
                    }
                })
                .setNegativeButton("Cancel", null);
        // Create the AlertDialog
        builder.create().show();

    }


    public void editProductDialog(final int id, String name, int amount, boolean bought){
        final Dialog editProductDlg = new Dialog(mContext);
        editProductDlg.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        editProductDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editProductDlg.setContentView(R.layout.dialog_add_product);

        TextView title = (TextView) editProductDlg.findViewById(R.id.dialogTitle);
        title.setText("Update Product");

        EditText etName = (EditText) editProductDlg.findViewById(R.id.et_addProductName);
        etName.setText(name);

        EditText etAmount = (EditText) editProductDlg.findViewById(R.id.et_addProductAmount);
        etAmount.setText(String.valueOf(amount));

        LinearLayout statusLayout = (LinearLayout) editProductDlg.findViewById(R.id.LinearLayout_product_status);
        statusLayout.setVisibility(View.VISIBLE);

        Spinner mSpinner = (Spinner) editProductDlg.findViewById(R.id.spinner_bought);
        if (bought) {
            mSpinner.setSelection(1);
        } else {
            mSpinner.setSelection(0);
        }

        Button btnCancel = (Button)editProductDlg.findViewById(R.id.dlgBtnAddProductCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                editProductDlg.cancel();
            }
        });

        Button btnSave = (Button)editProductDlg.findViewById(R.id.dlgBtnAddProductSave);
        btnSave.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText newProduct = (EditText) editProductDlg.findViewById(R.id.et_addProductName);
                String pName = newProduct.getText().toString();

                EditText newAmount = (EditText) editProductDlg.findViewById(R.id.et_addProductAmount);
                Integer pAmount = Integer.valueOf(newAmount.getText().toString());

                Spinner mSpinner = (Spinner) editProductDlg.findViewById(R.id.spinner_bought);
                Integer bought = mSpinner.getSelectedItemPosition();

                DB.updateProduct(id, pName, pAmount,bought);
                Log.d(TAG, "update product: " + id + "," + pName + "," + pAmount + "," + bought);
                updateShopList();
                editProductDlg.cancel();
            }
        });
        editProductDlg.show();
    }

    private void updateShopList() {
        changeCursor(DB.getAllProduct());
        notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.adapter_shop_list, parent, false);
    }

}
