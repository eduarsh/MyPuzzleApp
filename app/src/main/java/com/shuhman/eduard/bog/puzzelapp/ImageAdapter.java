package com.shuhman.eduard.bog.puzzelapp;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by B.o.G on 06/05/2016.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return 15;// mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        int row_no = position / 4;
        int col_no = position % 4;
        Main2Activity.imageViewsArray[row_no][col_no] = new ImageView(mContext);
        Main2Activity.imageViewsArray[row_no][col_no].setId(position);
        Main2Activity.imageViewsArray[row_no][col_no].setImageBitmap(Main2Activity.bMap[row_no][col_no]);
        Main2Activity.imageViewsArray[row_no][col_no].setScaleType(ImageView.ScaleType.CENTER_CROP);
        Main2Activity.imageViewsArray[row_no][col_no].setLayoutParams(new GridView.LayoutParams((int) convertDpToPixel(60, mContext), (int) convertDpToPixel(60, mContext)));
        Main2Activity.imageViewsArray[row_no][col_no].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Main2Activity.animationOnProcces) {
                    Main2Activity.animationOnProcces = true;
                    for (int i = 0; i < 4; i++)
                        for (int j = 0; j < 4; j++) {
                            if (Main2Activity.imageViewsArray[i][j] != null && ((ImageView) v).getId() == Main2Activity.imageViewsArray[i][j].getId()) {
                                // Top
                                if (i != 0) {
                                    if (Main2Activity.imageViewsArray[i - 1][j] == null) {
                                        // Move top
                                        ViewCompat.animate(Main2Activity.imageViewsArray[i][j]).setDuration(110).translationYBy(convertDpToPixel(-60, mContext)).start();
                                        Main2Activity.imageViewsArray[i - 1][j] = Main2Activity.imageViewsArray[i][j];
                                        Main2Activity.imageViewsArray[i][j] = null;
                                        Main2Activity.animationOnProcces = false;
                                        return;
                                    }
                                }
                                if (i != 3) {
                                    if (Main2Activity.imageViewsArray[i + 1][j] == null) {
                                        // Move down
                                        ViewCompat.animate(Main2Activity.imageViewsArray[i][j]).setDuration(110).translationYBy(convertDpToPixel(60, mContext)).start();
                                        Main2Activity.imageViewsArray[i + 1][j] = Main2Activity.imageViewsArray[i][j];
                                        Main2Activity.imageViewsArray[i][j] = null;
                                        Main2Activity.animationOnProcces = false;
                                        return;
                                    }
                                }
                                if (j != 0) {
                                    if (Main2Activity.imageViewsArray[i][j - 1] == null) {
                                        // Move left
                                        ViewCompat.animate(Main2Activity.imageViewsArray[i][j]).setDuration(110).translationXBy(convertDpToPixel(-60, mContext)).start();
                                        Main2Activity.imageViewsArray[i][j - 1] = Main2Activity.imageViewsArray[i][j];
                                        Main2Activity.imageViewsArray[i][j] = null;
                                        Main2Activity.animationOnProcces = false;
                                        return;
                                    }
                                }
                                if (j != 3) {
                                    if (Main2Activity.imageViewsArray[i][j + 1] == null) {
                                        // Move rigth
                                        ViewCompat.animate(Main2Activity.imageViewsArray[i][j]).setDuration(110).translationXBy(convertDpToPixel(60, mContext)).start();
                                        Main2Activity.imageViewsArray[i][j + 1] = Main2Activity.imageViewsArray[i][j];
                                        Main2Activity.imageViewsArray[i][j] = null;
                                        Main2Activity.animationOnProcces = false;
                                        return;
                                    }
                                }
                            }
                        }
                }
                Main2Activity.animationOnProcces = false;
            }
        });
        return Main2Activity.imageViewsArray[row_no][col_no];
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
