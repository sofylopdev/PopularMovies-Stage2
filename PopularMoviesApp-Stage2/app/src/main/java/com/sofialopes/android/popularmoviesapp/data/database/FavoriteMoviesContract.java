package com.sofialopes.android.popularmoviesapp.data.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Created by Sofia on 2/28/2018.
 */

public class FavoriteMoviesContract {

    public static final String AUTHORITY = "com.sofialopes.android.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //accessing the table favorite movies
    public static final String PATH_MOVIES = "favoriteMovies";
    //accessing the table videos
    public static final String PATH_VIDEOS = "videos";
    //accessing the table reviews
    public static final String PATH_REVIEWS = "reviews";

    private FavoriteMoviesContract() {
    }

    //I chose to not extend BaseColumns because i'm using the movieId as a primary key
    public static final class MoviesEntry {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String MOVIES_TABLE_NAME = "favoriteMovies";

        public static final String COLUMN_ID = "movieId";
        public static final String COLUMN_TITLE = "movieTitle";
        public static final String COLUMN_POSTER = "moviePoster";
        public static final String COLUMN_BACKDROP = "movieBackdrop";
        public static final String COLUMN_OVERVIEW = "movieOverview";
        public static final String COLUMN_RATING = "movieRating";
        public static final String COLUMN_RELEASE = "movieRelease";
        public static final String COLUMN_RUNTIME = "movieRuntime";
    }

    public static final class VideosEntry implements BaseColumns {

        public static final Uri CONTENT_VIDEOS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String VIDEOS_TABLE_NAME = "videos";

        public static final String COLUMN_KEY = "videoKey";
        public static final String COLUMN_NAME = "videoName";
        public static final String COLUMN_MOVIE_ID = "movieId";
    }

    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_REVIEWS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String REVIEWS_TABLE_NAME = "reviews";

        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_MOVIE_ID = "movieId";
    }
}
