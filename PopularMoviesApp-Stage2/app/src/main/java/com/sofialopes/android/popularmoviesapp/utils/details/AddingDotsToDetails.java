package com.sofialopes.android.popularmoviesapp.utils.details;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sofialopes.android.popularmoviesapp.utils.DisplayUtils;
import com.sofialopes.android.popularmoviesapp.R;

/**
 *
 * Created by Sofia on 3/13/2018.
 */

public class AddingDotsToDetails {

    public static void settingTheDots(final Context context, final int dotscount,
                                      RecyclerView recycler, LinearLayout dotsContainer) {

        if (dotscount > 1) {
            final ImageView[] dots = new ImageView[dotscount];

            for (int i = 0; i < dotscount; i++) {
                dots[i] = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                dots[i].setImageDrawable(ContextCompat.getDrawable(
                        context,
                        R.drawable.recycler_non_active_dots));

                //Setting different params if the phone is very small,
                // or else it won't show all the dots
                int smallestWidth = DisplayUtils.getSmallestWidth(context);
                if (dotscount > 18 && smallestWidth <= 320) {
                    params.setMargins(6, 0, 0, 0);
                } else {
                    params.setMargins(8, 2, 8, 0);
                }

                dotsContainer.addView(dots[i], params);
            }

            recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int offset = recyclerView.computeHorizontalScrollOffset();
                    int myCellWidth = recyclerView.getChildAt(0).getMeasuredWidth();
                    int position = offset / myCellWidth;

                    for (int i = 0; i < dotscount; i++) {
                        if (i == position) {//current position
                            dots[i].setImageDrawable(ContextCompat.getDrawable(
                                    context,
                                    R.drawable.recycler_active_dots));
                        } else {
                            dots[i].setImageDrawable(ContextCompat.getDrawable(
                                    context,
                                    R.drawable.recycler_non_active_dots));
                        }
                    }
                }
            });
        }
    }
}
