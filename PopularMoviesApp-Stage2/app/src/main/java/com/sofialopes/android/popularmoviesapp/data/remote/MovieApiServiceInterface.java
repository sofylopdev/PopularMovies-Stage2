package com.sofialopes.android.popularmoviesapp.data.remote;

import com.sofialopes.android.popularmoviesapp.data.models.MovieDetails;
import com.sofialopes.android.popularmoviesapp.data.models.MoviesResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 *
 * Created by Sofia on 2/27/2018.
 */

public interface MovieApiServiceInterface {

    @GET("movie/{preference}")
    Call<MoviesResults> getMovies(
            @Path("preference") String preference,
            @Query("api_key") String apiKey,
            @Query("page") int page);

    @GET("movie/{movieId}")
    Call<MovieDetails> getMovieDetails(
            @Path("movieId")  int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String videosAndReviews);
}
