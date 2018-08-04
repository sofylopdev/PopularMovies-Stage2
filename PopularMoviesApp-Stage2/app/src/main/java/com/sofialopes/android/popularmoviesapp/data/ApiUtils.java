package com.sofialopes.android.popularmoviesapp.data;

import android.net.Uri;

import com.sofialopes.android.popularmoviesapp.data.remote.MovieApiServiceInterface;
import com.sofialopes.android.popularmoviesapp.data.remote.RetrofitClient;

/**
 *
 * Created by Sofia on 2/27/2018.
 */

public class ApiUtils {

    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/";

    private static final String BASE_IMAGE_URI = "http://image.tmdb.org";
    private static final String COMMON_IMAGES_PATH = "t/p";
    public static final String POSTER_SIZE = "w185";
    public static final String BACKDROP_SIZE = "w342";//w500

    public static String uriImagesParser(String imageSize, String finalPath) {

        Uri builtUri = Uri.parse(BASE_IMAGE_URI)
                .buildUpon()
                .appendEncodedPath(COMMON_IMAGES_PATH)
                .appendPath(imageSize)
                .appendPath(finalPath)
                .build();
        return builtUri.toString();
    }

    public static MovieApiServiceInterface getMovieApiService() {
        return RetrofitClient.getClient(MOVIES_BASE_URL).create(MovieApiServiceInterface.class);
    }
}
