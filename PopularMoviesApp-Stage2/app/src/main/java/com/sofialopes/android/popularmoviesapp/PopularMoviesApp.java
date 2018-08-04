package com.sofialopes.android.popularmoviesapp;

import android.app.Application;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 *
 * Created by Sofia on 3/23/2018.
 */

public class PopularMoviesApp extends Application {

    //This was created to so that images load even if the device is offline
    //I've used this guide:
    // https://blog.fossasia.org/cache-thumbnails-and-images-using-picasso-in-open-event-android/
    public static Picasso picassoWithCache;

    @Override
    public void onCreate() {
        super.onCreate();

        //Initializing Cache
        File httpCacheDirectory = new File(getCacheDir(), "picasso-cache");
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder().cache(cache);

        //Initializing Picasso with cache
        picassoWithCache = new Picasso.Builder(this).downloader(
                new OkHttp3Downloader(okHttpClientBuilder.build())).build();

    }
}
