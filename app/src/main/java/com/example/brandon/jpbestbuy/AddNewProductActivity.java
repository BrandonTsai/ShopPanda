package com.example.brandon.jpbestbuy;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddNewProductActivity extends AppCompatActivity {

    private static int RESULT_GALLERY_IMAGE = 1;
    private static int RESULT_CAMERA_IMAGE = 2;
    private String filename;
    private String filepath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        filename = df.format(Calendar.getInstance().getTime()) + ".jpg";

        initSaveButton();
        initCancelButton();
        initCameraButton();
        initGalleryButton();

    }

    private void initGalleryButton() {
        Button btn = (Button) findViewById(R.id.button_new_product_gallery);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_GALLERY_IMAGE);
            }
        });
    }

    private void initCameraButton() {
        Button btn = (Button) findViewById(R.id.button_new_product_camera);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(android.os.Environment.getExternalStorageDirectory(), filename);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(intent, RESULT_CAMERA_IMAGE);
            }
        });
    }

    private void initCancelButton() {
        Button btn = (Button) findViewById(R.id.button_new_product_cancel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initSaveButton() {
        Button btn = (Button) findViewById(R.id.button_new_product_save);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = (EditText) findViewById(R.id.editText_product_name);
                String pName = etName.getText().toString();
                EditText etAmount = (EditText) findViewById(R.id.editText_product_amount);
                Integer pAmount = Integer.valueOf(etAmount.getText().toString());
                DB.addProduct(pName, pAmount,filepath);
                setResult(RESULT_OK);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK ) {
            if (requestCode == RESULT_GALLERY_IMAGE && null != data) {
                Uri selectedImage = data.getData();
                Log.d("X", "gallery img URI:" + data.toString());
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filepath = cursor.getString(columnIndex);
                cursor.close();

                Log.d("X", "gallery img:" + filepath);
                // String picturePath contains the path of selected Image
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(filepath));

            } else if (requestCode == RESULT_CAMERA_IMAGE){
                File f = new File(android.os.Environment.getExternalStorageDirectory(), filename);
                filepath = f.getAbsolutePath();
                Log.d("X", "camera img:" + filepath);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(filepath));
            }
        }
    }

}
