package com.sofialopes.android.popularmoviesapp.data.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.MoviesEntry;
import com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.VideosEntry;

/**
 *
 * Created by Sofia on 2/28/2018.
 */

public class FavoriteMoviesContentProvider extends ContentProvider {

    public static final String TAG_PROVIDER = FavoriteMoviesContract.class.getName();

    private MoviesDbHelper mMoviesDbHelper;

    private static final int CODE_MOVIES = 100;
    private static final int CODE_MOVIES_WITH_ID = 101;

    private static final int CODE_VIDEOS = 200;
    private static final int CODE_VIDEOS_WITH_ID = 201;

    private static final int CODE_REVIEWS = 300;
    private static final int CODE_REVIEWS_WITH_ID = 301;

    private static final String SELECTION_BY_MOVIE_ID = "movieId=?";


    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //directories
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_MOVIES, CODE_MOVIES);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_VIDEOS, CODE_VIDEOS);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_REVIEWS, CODE_REVIEWS);
        //single item (# = id)
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_MOVIES + "/#", CODE_MOVIES_WITH_ID);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_VIDEOS + "/#", CODE_VIDEOS_WITH_ID);
        uriMatcher.addURI(FavoriteMoviesContract.AUTHORITY, FavoriteMoviesContract.PATH_REVIEWS + "/#", CODE_REVIEWS_WITH_ID);

        return uriMatcher;
    }

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase database = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case CODE_MOVIES:
                long id = database.insert(
                        MoviesEntry.MOVIES_TABLE_NAME, null, values);

                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MoviesEntry.CONTENT_URI, id);
                    Log.d(TAG_PROVIDER, "Success inserting movie: " + returnUri);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case CODE_VIDEOS:
                long idVideos = database.insert(
                        VideosEntry.VIDEOS_TABLE_NAME, null, values);

                if (idVideos > 0) {
                    returnUri = ContentUris.withAppendedId(VideosEntry.CONTENT_VIDEOS_URI, idVideos);
                    Log.d(TAG_PROVIDER, "Success inserting video: " + returnUri);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            case CODE_REVIEWS:
                long idReviews = database.insert(
                        FavoriteMoviesContract.ReviewsEntry.REVIEWS_TABLE_NAME, null, values);

                if (idReviews > 0) {
                    returnUri = ContentUris.withAppendedId(
                            FavoriteMoviesContract.ReviewsEntry.CONTENT_REVIEWS_URI, idReviews);
                    Log.d(TAG_PROVIDER, "Success inserting review: " + returnUri);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Trying to insert unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase database = mMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnCursor;
        switch (match) {
            case CODE_MOVIES:
                returnCursor = database.query(MoviesEntry.MOVIES_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.d(TAG_PROVIDER, "Getting all data.");
                break;

            case CODE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = SELECTION_BY_MOVIE_ID;
                String[] mSelectionArgs = new String[]{id};

                returnCursor = database.query(MoviesEntry.MOVIES_TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );

                Log.d(TAG_PROVIDER, "Getting movie with ID: " + id);
                break;

            case CODE_VIDEOS_WITH_ID:
                String idMovie = uri.getPathSegments().get(1);
                String mSelectionVideos = SELECTION_BY_MOVIE_ID;
                String[] mSelectionArgsVideos = new String[]{idMovie};

                returnCursor = database.query(VideosEntry.VIDEOS_TABLE_NAME,
                        projection,
                        mSelectionVideos,
                        mSelectionArgsVideos,
                        null,
                        null,
                        sortOrder
                );

                Log.d(TAG_PROVIDER, "Getting video from movie with ID: " + idMovie);
                break;

            case CODE_REVIEWS_WITH_ID:
                idMovie = uri.getPathSegments().get(1);
                mSelectionVideos = SELECTION_BY_MOVIE_ID;
                mSelectionArgsVideos = new String[]{idMovie};

                returnCursor = database.query(FavoriteMoviesContract.ReviewsEntry.REVIEWS_TABLE_NAME,
                        projection,
                        mSelectionVideos,
                        mSelectionArgsVideos,
                        null,
                        null,
                        sortOrder
                );

                Log.d(TAG_PROVIDER, "Getting review from movie with ID: " + idMovie);
                break;

            default:
                throw new UnsupportedOperationException("Trying to query unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase database = mMoviesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        int numberRowsDeleted;
        switch (match) {
            case CODE_MOVIES:
                numberRowsDeleted = database.delete(MoviesEntry.MOVIES_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = SELECTION_BY_MOVIE_ID;
                String[] mSelectionArgs = new String[]{id};

                numberRowsDeleted = database.delete(MoviesEntry.MOVIES_TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Trying to delete unknown uri: " + uri);
        }
        if (numberRowsDeleted != 0) {
            Log.d(TAG_PROVIDER,
                    "Sucessfully deleted " + numberRowsDeleted + "row. Uri: " + uri);
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new RuntimeException("Update is not implemented.");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("GetType is not implemented.");
    }
}
