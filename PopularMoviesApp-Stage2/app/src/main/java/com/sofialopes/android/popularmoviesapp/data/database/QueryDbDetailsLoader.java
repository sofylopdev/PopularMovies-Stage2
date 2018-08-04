package com.sofialopes.android.popularmoviesapp.data.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

/**
 *
 * Created by Sofia on 3/19/2018.
 */

public class QueryDbDetailsLoader extends AsyncTaskLoader<Cursor> {

    private Uri mCurrentMovieUri;
    private Cursor mCursor;

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mCursor != null )
            deliverResult(mCursor);
        else {
            forceLoad();
        }
    }

    public QueryDbDetailsLoader(@NonNull Context context, Uri currentMovieUri ) {
        super(context);
        this.mCurrentMovieUri = currentMovieUri;
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {
        mCursor = getContext().getContentResolver().query(mCurrentMovieUri,
                null, null, null, null);
        return mCursor;


    }
    @Override
    public void deliverResult(Cursor data) {
        mCursor = data;
        super.deliverResult(data);
    }
}
