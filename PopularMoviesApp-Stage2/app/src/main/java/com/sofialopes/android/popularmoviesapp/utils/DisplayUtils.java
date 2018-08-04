package com.sofialopes.android.popularmoviesapp.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.sofialopes.android.popularmoviesapp.R;

import static android.content.Context.WINDOW_SERVICE;

/**
 *
 * Created by Sofia on 2/24/2018.
 */

public final class DisplayUtils {
    private DisplayUtils(){
        throw new AssertionError("DisplayUtils don't need to be instantiated.");
    }

    private static DisplayMetrics getDisplayMetrics(){
        return Resources.getSystem().getDisplayMetrics();
    }

    private static float getDensity(){
        return getDisplayMetrics().density;
    }

    private static int getWidthPixels(){
        return getDisplayMetrics().widthPixels;
    }

    public static int getNumberOfColumns(){
        int dpWidth = (int) (getWidthPixels() / getDensity());
        return (dpWidth / 185) + 1;
    }

    public static int getPosterImageWidth(Context context){
        int screenWidth = DisplayUtils.getWidthPixels();
        int noOfColumns = DisplayUtils.getNumberOfColumns();

        int spacing = 2 * (context.getResources().getDimensionPixelOffset(R.dimen.frame_spacing));//left and right; top and bottom
        int imageWidth = ((screenWidth - spacing ) / (noOfColumns));
        return imageWidth;
    }

    public static int getPosterImageHeight(int imageWidth){
        int imageHeight = (int) ((imageWidth)* 1.5 );
        return imageHeight;
    }


    public static int getSmallestWidth(Context context){
        Configuration config = context.getResources().getConfiguration();
        int smallestWidth = config.smallestScreenWidthDp;
        return smallestWidth;
    }


    private static int getOrientation(Context context){
        Display display = ((WindowManager)
                context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        return orientation;
    }

    public static Drawable setErrorHorizontalPlaceholder(Context context){
        int orientation = getOrientation(context);

        if(orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270){
            return ContextCompat.getDrawable(context,
                    R.drawable.img_not_avail_horiz_landscape);
        }else{
            return ContextCompat.getDrawable(context,
                    R.drawable.image_not_available_horizontal);
        }
    }
}


