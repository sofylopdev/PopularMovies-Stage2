package com.sofialopes.android.popularmoviesapp.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.MoviesEntry;
import static com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.ReviewsEntry;
import static com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.VideosEntry;

/**
 *
 * Created by Sofia on 2/28/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoritemovies.db";

    private static final int DATABASE_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE IF NOT EXISTS " +
                MoviesEntry.MOVIES_TABLE_NAME + " ("

                + MoviesEntry.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL,"
                + MoviesEntry.COLUMN_POSTER + " TEXT,"
                + MoviesEntry.COLUMN_BACKDROP + " TEXT,"
                + MoviesEntry.COLUMN_OVERVIEW + " TEXT,"
                + MoviesEntry.COLUMN_RATING + " INTEGER, "
                + MoviesEntry.COLUMN_RELEASE + " TEXT, "
                + MoviesEntry.COLUMN_RUNTIME + " INTEGER"
                + ");";

        //On delete cascade was implemented because i want to delete all the videos and reviews
        // connected to the movie that was removed from the database

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                VideosEntry.VIDEOS_TABLE_NAME + " ("

                + VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + VideosEntry.COLUMN_KEY + " TEXT NOT NULL,"
                + VideosEntry.COLUMN_NAME + " TEXT,"
                + VideosEntry.COLUMN_MOVIE_ID + " INTEGER REFERENCES " + MoviesEntry.MOVIES_TABLE_NAME +
                "(" + MoviesEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ");";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                ReviewsEntry.REVIEWS_TABLE_NAME + " ("

                + ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL,"
                + ReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL,"
                + ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER REFERENCES " + MoviesEntry.MOVIES_TABLE_NAME +
                "(" + MoviesEntry.COLUMN_ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.MOVIES_TABLE_NAME);
        onCreate(db);
    }
}
