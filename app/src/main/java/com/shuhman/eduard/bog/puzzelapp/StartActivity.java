package com.shuhman.eduard.bog.puzzelapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StartActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int START_X = 10;
    private static final int START_Y = 15;
    private static final int WIDTH_PX = 100;
    private static final int HEIGHT_PX = 100;
    ImageView cameraImage;
    Button browse, startCamera, startGame;
    Bitmap img;
    Drawable original;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        cameraImage = (ImageView) findViewById(R.id.imgSelect);
        browse = (Button) findViewById(R.id.btnBrows);
        startCamera = (Button) findViewById(R.id.btnCamera);
        startGame = (Button) findViewById(R.id.btnStart);

        if (!hasCamera())
            startGame.setEnabled(false);
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void setPicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Bundle extra = data.getExtras();
//                Bitmap photo = (Bitmap)extra.get("data");
//                cameraImage.setImageBitmap(photo);
//
//            }
//        }
//
//    }
    public void gameStart(View view) {
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                cameraImage.setImageBitmap(thumbnail);

            } else if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                cameraImage.setImageBitmap(bm);
            }
        }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//            if(resultCode == RESULT_OK) {
//
//                switch (requestCode) {
//
//                    case SELECT_PICTURE:
//                        Uri selectedImageUri = data.getData();
//                        String selectedImagePath = getPath(selectedImageUri);
//                        System.out.println("Image Path : " + selectedImagePath);
//                        cameraImage.setImageURI(selectedImageUri);
////                        Bundle extra = data.getExtras();
////                        Bitmap photo = (Bitmap)extra.get("data");
//////                Bitmap newBitmap = Bitmap.createBitmap(photo, START_X, START_Y, WIDTH_PX, HEIGHT_PX, null, false);
////                        Bitmap newBitmap = Bitmap.createScaledBitmap(photo,500,500,true);
//////                Bitmap img = getRoundedRectBitmap(newBitmap, 200);
////                        cameraImage.setImageBitmap(photo);
//                        break;
//
//                }
//            }
//    }
//    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
//        Bitmap result = null;
//        try {
//            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(result);
//
//            int color = 0xff424242;
//            Paint paint = new Paint();
//            Rect rect = new Rect(0, 0, 200, 200);
//
//            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(color);
//            canvas.drawCircle(150, 150, 150, paint);
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        } catch (NullPointerException e) {
//        } catch (OutOfMemoryError o) {
//        }
//        return result;
//    }
//
//    public String getPath(Uri uri) {
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = managedQuery(uri, projection, null, null, null);
//        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        cursor.moveToFirst();
//        return cursor.getString(column_index);
//    }
    }
}