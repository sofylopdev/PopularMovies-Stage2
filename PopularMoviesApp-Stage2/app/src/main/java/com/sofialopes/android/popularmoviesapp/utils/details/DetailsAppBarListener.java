package com.sofialopes.android.popularmoviesapp.utils.details;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.R;

import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.MIN_VERSION;
import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.MIN_VERSION_WITH_CONSTRAINT_LAYOUT;

/**
 *
 * Created by Sofia on 3/6/2018.
 */

public class DetailsAppBarListener implements AppBarLayout.OnOffsetChangedListener {

    private Context mContext;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private TextView mTitleInImageTv;
    private int mVersion;
    private String mTitle;

    public DetailsAppBarListener(Context context,
                                 CollapsingToolbarLayout collapsingToolbarLayout, Toolbar toolbar,
                                 TextView titleInImage, int version, String title) {

        this.mContext = context;
        this.mCollapsingToolbarLayout = collapsingToolbarLayout;
        this.mToolbar = toolbar;
        this.mTitleInImageTv = titleInImage;
        this.mVersion = version;
        this.mTitle = title;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int scrollRange = appBarLayout.getTotalScrollRange();

        if (scrollRange + verticalOffset <= 10) {
            //Collapsed AppBar
            mCollapsingToolbarLayout.setTitle(mTitle);
            mToolbar.setBackgroundColor(Color.TRANSPARENT);

            if (mVersion >= MIN_VERSION && mVersion < MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
                mTitleInImageTv.setVisibility(View.INVISIBLE);
            }

        } else if ((scrollRange + verticalOffset) <= (scrollRange - 20)
                && (scrollRange + verticalOffset) >= 0) {
            // In between
            // (necessary because while moving from extended to collapsed,
            // toolbar would get weird)
            if (mVersion >= MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
                mCollapsingToolbarLayout.setTitle(" ");
                mToolbar.setBackgroundColor(Color.TRANSPARENT);
            }

        } else {
            //Extended appBar
            mCollapsingToolbarLayout.setTitle(" ");
            mToolbar.setBackground(ContextCompat.getDrawable(
                    mContext, R.drawable.gradient_bg));

            if (mVersion >= MIN_VERSION && mVersion < MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
                mTitleInImageTv.setVisibility(View.VISIBLE);
            }
        }
    }
}
