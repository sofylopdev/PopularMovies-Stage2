package com.sofialopes.android.popularmoviesapp.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.R;

/**
 *
 * Created by Sofia on 3/23/2018.
 */

public class SpinnerAdapter extends BaseAdapter {

    private Context mContext;
    private TypedArray mDrawables;
    private String[] mSpinnerOptions;


    public SpinnerAdapter(Context applicationContext, TypedArray drawables, String[] spinnerOptions) {
        this.mContext = applicationContext;
        this.mSpinnerOptions = spinnerOptions;
        this.mDrawables = drawables;
    }

    @Override
    public int getCount() {
        return mSpinnerOptions.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        // if(view == null){ <- if i added this it would repeat the first item (popular) twice
        // when the second (top rated) was selected
        view = LayoutInflater.from(mContext)
                .inflate(R.layout.spinner_item, viewGroup, false);

        ImageView icon = view.findViewById(R.id.icon_spinner);
        TextView names = view.findViewById(R.id.text_spinner);

        icon.setImageResource(mDrawables.getResourceId(i, -1));
        if (i == 2) {
            //adding a touch of blue to the favorites icon
            icon.setColorFilter(ContextCompat.getColor(mContext, R.color.spinner_icons_color),
                    PorterDuff.Mode.MULTIPLY);
        }
        String selection = mSpinnerOptions[i];
        names.setText(selection);
        icon.setContentDescription(selection);
        // }

        return view;
    }
}