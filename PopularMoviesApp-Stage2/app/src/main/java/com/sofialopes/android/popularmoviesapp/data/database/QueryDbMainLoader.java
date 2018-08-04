package com.sofialopes.android.popularmoviesapp.data.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.MoviesEntry;
import com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.ReviewsEntry;
import com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.VideosEntry;
import com.sofialopes.android.popularmoviesapp.data.models.Movie;
import com.sofialopes.android.popularmoviesapp.data.models.ReviewsResults;
import com.sofialopes.android.popularmoviesapp.data.models.VideosResults;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sofia on 3/19/2018.
 */

public class QueryDbMainLoader extends AsyncTaskLoader<List<Movie>> {

    private static final String DB_TO_MOVIE = QueryDbMainLoader.class.getName();
    private List<Movie> mMoviesList = new ArrayList<>();

    public QueryDbMainLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mMoviesList != null && !mMoviesList.isEmpty())
            deliverResult(mMoviesList);
        else {
            forceLoad();
        }
    }

    @Nullable
    @Override
    public List<Movie> loadInBackground() {

        Cursor movieCursor = getContext().getContentResolver().query(
                MoviesEntry.CONTENT_URI,
                null, null, null, null);

        if (movieCursor != null) {
            //If we have a movie in the database, get all values from it
            while (movieCursor.moveToNext()) {
                int idMovie = movieCursor.getInt(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_ID));
                String title = movieCursor.getString(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_TITLE));
                String poster = movieCursor.getString(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_POSTER));
                String backdrop = movieCursor.getString(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_BACKDROP));
                String overview = movieCursor.getString(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_OVERVIEW));
                double rating = movieCursor.getInt(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_RATING));
                String release = movieCursor.getString
                        (movieCursor.getColumnIndex(MoviesEntry.COLUMN_RELEASE));
                int runtime = movieCursor.getInt(
                        movieCursor.getColumnIndex(MoviesEntry.COLUMN_RUNTIME));

                //Getting all videos from that movie
                Uri currentVideosUri = VideosEntry.CONTENT_VIDEOS_URI.buildUpon().
                        appendPath(idMovie + "").build();
                Cursor cursorVideos = getContext().getContentResolver().query(currentVideosUri,
                        null, null, null, null);

                List<VideosResults> listVideos = new ArrayList<>();
                if (cursorVideos.moveToNext()) {
                    cursorVideos.moveToFirst();
                    //adding first video
                    addingVideosData(cursorVideos, listVideos);
                    //adding rest of the videos
                    while (cursorVideos.moveToNext()) {
                        addingVideosData(cursorVideos, listVideos);
                    }
                } else {
                    Log.i(DB_TO_MOVIE, "NO VIDEOS in CURSOR");
                }
                cursorVideos.close();

                //Getting all reviews from that movie
                Uri currentReviewsUri = ReviewsEntry.CONTENT_REVIEWS_URI.buildUpon().
                        appendPath(idMovie + "").build();
                Cursor cursorReviews = getContext().getContentResolver().query(currentReviewsUri,
                        null, null, null, null);

                List<ReviewsResults> listReviews = new ArrayList<>();
                if (cursorReviews.moveToNext()) {
                    cursorReviews.moveToFirst();
                    //adding first review
                    addingReviewsData(cursorReviews, listReviews);
                    //adding rest of the reviews
                    while (cursorReviews.moveToNext()) {
                        addingReviewsData(cursorReviews, listReviews);
                    }
                } else {
                    Log.i(DB_TO_MOVIE, "NO REVIEWS in CURSOR");
                }
                cursorReviews.close();


                //Creating the movie and adding all the data to it
                Movie movie = new Movie(
                        idMovie, title, poster, backdrop, overview, rating, release, runtime);
                if (!listVideos.isEmpty())
                    movie.setVideosResults(listVideos);

                if (!listReviews.isEmpty())
                    movie.setReviewsResults(listReviews);

                //Adding the movie to the list of favorites
                mMoviesList.add(movie);
            }
            movieCursor.close();
        }

        return mMoviesList;
    }

    @Override
    public void deliverResult(List<Movie> data) {
        mMoviesList = data;
        super.deliverResult(data);
    }

    private void addingVideosData(Cursor cursorVideos, List<VideosResults> listVideos) {
        int movieId = cursorVideos.getInt(
                cursorVideos.getColumnIndex(VideosEntry.COLUMN_MOVIE_ID));
        String videoName = cursorVideos.getString(
                cursorVideos.getColumnIndex(VideosEntry.COLUMN_NAME));
        String videoKey = cursorVideos.getString(
                cursorVideos.getColumnIndex(VideosEntry.COLUMN_KEY));
        listVideos.add(new VideosResults(movieId, videoName, videoKey));
    }

    private void addingReviewsData(Cursor cursorReviews, List<ReviewsResults> listReviews) {
        int movieId = cursorReviews.getInt(
                cursorReviews.getColumnIndex(ReviewsEntry.COLUMN_MOVIE_ID));
        String author = cursorReviews.getString(
                cursorReviews.getColumnIndex(ReviewsEntry.COLUMN_AUTHOR));
        String content = cursorReviews.getString(
                cursorReviews.getColumnIndex(ReviewsEntry.COLUMN_CONTENT));
        listReviews.add(new ReviewsResults(movieId, author, content));
    }
}
