package com.shuhman.eduard.bog.puzzelapp;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Main2Activity extends Activity implements View.OnClickListener {

    public static Bitmap[][] bMap;
    public static Bitmap bitMapPic;
    public static ImageView[][] imageViewsArray = new ImageView[4][4];
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static RequestQueue queue;
    Button camera, share;
    public static Boolean animationOnProcces = false;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        queue = Volley.newRequestQueue(this);
        if (getIntent().getData()!= null) {
            checkForIntent();

        } else {
            bitMapPic = setIamgeTop();
            callGrid(bitMapPic);
        }
        camera = (Button) findViewById(R.id.btn_camera);
        Button btn_share = (Button) findViewById(R.id.btn_share);
        btn_share.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                shacreIt();
            }
        });
        if (!hasCamera())
            camera.setEnabled(false);
    }

    private void checkForIntent() {

        Uri data = getIntent().getData();
        String scheme = data.getScheme(); // "mypuzzleapp"
        String host = data.getHost(); // "image"
        List<String> params = data.getPathSegments();
        String first = params.get(0); // "image number from the server"
        if (scheme != null){
            if (host != null){
                if (first != null){
                    downloadFromServer(first);
                }
            }
        }
    }

    private void downloadFromServer(String p) {
        Log.i("EDIK", p);
        String url = "http://www.mypuzzleapp.com/image.php?id=";
        ImageRequest request = new ImageRequest(url + p,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        ImageView image = (ImageView) findViewById(R.id.top_image);
                        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        image.setImageBitmap(bitmap);
                        callGrid(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        queue.add(request);
    }

    private void uploadToServer(final Bitmap bitMap) {
        // Instantiate the RequestQueue.
        String url = "http://www.mypuzzleapp.com/image";

// Request a string response from the provided URL.
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                Log.i("CHEKER", resultResponse);
                id = resultResponse;
                // parse success output
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("upload", new DataPart("abc.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), bitMap), "image/jpeg"));

                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

// Add the request to the RequestQueue.
        queue.add(multipartRequest);
    }

    private void shacreIt() {

//        String str_text = "<a href=puzzelapp://other/>PuzzleApp</a>";
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        // Add data to the intent, the receiving app will decide
        share.putExtra(Intent.EXTRA_TEXT,  id);
        startActivity(Intent.createChooser(share, "Share link!"));
    }


    private void callGrid(Bitmap bitMapPic) {
        imageViewsArray = new ImageView[4][4];
        bMap = splitBitmap(bitMapPic, 4, 4);
        uploadToServer(bitMapPic);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));
    }

    private boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private Bitmap setIamgeTop() {
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.top_bg2);
        // Resize the bitmap to 800x800 (width x height)
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, 800, 800, true);
        // Loads the resized Bitmap into an ImageView
        ImageView image = (ImageView) findViewById(R.id.top_image);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);//setImageBitmap(bMapScaled);
        image.setImageBitmap(bMapScaled);
        return bMapScaled;

    }

    @Override
    public void onClick(View v) {
    }

    public Bitmap[][] splitBitmap(Bitmap bitmap, int xCount, int yCount) {
        // Allocate a two dimensional array to hold the individual images.
        Bitmap[][] bitmaps = new Bitmap[xCount][yCount];
        int width, height;
        // Divide the original bitmap width by the desired vertical column count
        width = bitmap.getWidth() / xCount;
        // Divide the original bitmap height by the desired horizontal row count
        height = bitmap.getHeight() / yCount;
        // Loop the array and create bitmaps for each coordinate
        for (int x = 0; x < xCount; ++x) {
            for (int y = 0; y < yCount; ++y) {
                // Create the sliced bitmap
                bitmaps[x][y] = Bitmap.createBitmap(bitmap, x * width, y * height, width, height);
            }
        }
        // Return the array
        return bitmaps;
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                Bitmap bitMapPic = Bitmap.createScaledBitmap(thumbnail, 800, 800, false);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitMapPic.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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
                ImageView image = (ImageView) findViewById(R.id.top_image);
                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                image.setImageBitmap(bitMapPic);
                callGrid(bitMapPic);
            }
        }
    }
}
