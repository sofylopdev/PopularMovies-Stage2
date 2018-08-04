package com.sofialopes.android.popularmoviesapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sofialopes.android.popularmoviesapp.PopularMoviesApp;
import com.sofialopes.android.popularmoviesapp.R;
import com.sofialopes.android.popularmoviesapp.utils.ConnectivityReceiver;
import com.sofialopes.android.popularmoviesapp.utils.ConstantsClass;
import com.sofialopes.android.popularmoviesapp.utils.DisplayUtils;
import com.sofialopes.android.popularmoviesapp.utils.details.AddingDotsToDetails;
import com.sofialopes.android.popularmoviesapp.utils.details.DetailsAppBarListener;
import com.sofialopes.android.popularmoviesapp.adapters.ReviewsAdapter;
import com.sofialopes.android.popularmoviesapp.adapters.VideosAdapter;
import com.sofialopes.android.popularmoviesapp.data.ApiUtils;
import com.sofialopes.android.popularmoviesapp.data.database.QueryDbDetailsLoader;
import com.sofialopes.android.popularmoviesapp.data.models.Movie;
import com.sofialopes.android.popularmoviesapp.data.models.MovieDetails;
import com.sofialopes.android.popularmoviesapp.data.models.ReviewsResults;
import com.sofialopes.android.popularmoviesapp.data.models.VideosResults;
import com.sofialopes.android.popularmoviesapp.data.remote.MovieApiServiceInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sofialopes.android.popularmoviesapp.data.ApiUtils.BACKDROP_SIZE;
import static com.sofialopes.android.popularmoviesapp.data.ApiUtils.POSTER_SIZE;
import static com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.MoviesEntry;
import static com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.ReviewsEntry;
import static com.sofialopes.android.popularmoviesapp.data.database.FavoriteMoviesContract.VideosEntry;


public class DetailActivity extends AppCompatActivity implements VideosAdapter.AdapterClickHandler,
        ReviewsAdapter.AdapterClickHandler,
        ConnectivityReceiver.ConnectivityReceiverListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG_DETAILS = DetailActivity.class.getName();

    private AppBarLayout mAppBarLayout;
    private AppBarLayout.OnOffsetChangedListener mAppBarListener;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mBackgroundIv;
    private ImageView mPosterImageIv;
    private NestedScrollView mNestedScrollView;
    private ConstraintLayout mOuterConstraintLayout;
    private RelativeLayout mOuterRelativeLayout;

    private TextView mTitleTv;
    private TextView mReleaseDateTv;
    private TextView mVoteAverageTv;
    private TextView mOverviewTv;
    private TextView mTitleInImageTv;
    private FloatingActionButton mButtonAddMovie;
    private TextView mRuntimeTv;

    private TextView mVideosLabel;
    private RecyclerView mVideosRC;
    private LinearLayout mVideosDotsContainer;
    private VideosAdapter mVideosAdapter;

    private TextView mReviewsLabel;
    private LinearLayout mReviewsDotsContainer;
    private RecyclerView mReviewsRC;
    private ReviewsAdapter mReviewsAdapter;

    private int mDotsCount;
    private int mMovieId;
    private String mTitle;
    private String mPosterPath;
    private String mBackdropPath;
    private String mReleaseDate;
    private double mVoteAverage;
    private String mOverview;
    private int mRuntime;
    private List<ReviewsResults> mListOfReviews = new ArrayList<>();
    private List<VideosResults> mListOfVideos = new ArrayList<>();

    private int mVersion;
    private ConnectivityReceiver mConnectivityReceiver;

    private MovieApiServiceInterface mMovieApiService;
    public static final String APPEND_TO_RESPONSE = "videos,reviews";

    private Bundle mSavedInstanceState;
    public static final String SCROLL_POSITION = "LAYOUT_SCROLL_POSITION";
    private static final String VIDEOS_RECYCLER_POSITION = "video rc position";
    private static final String REVIEWS_RECYCLER_POSITION = "review rc position";

    public static final int DB_LOADER_DETAILS_ID_QUERY_FOR_BUTTON = 20;
    public static final int DB_LOADER_DETAILS_ID_QUERY_FOR_ADDING = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        this.mSavedInstanceState = savedInstanceState;
        mVersion = Build.VERSION.SDK_INT;

        mMovieApiService = ApiUtils.getMovieApiService();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_details);
        mAppBarLayout = findViewById(R.id.appbar);
        mBackgroundIv = findViewById(R.id.app_bar_background);
        mNestedScrollView = findViewById(R.id.nested_scroll);
        if (mVersion >= ConstantsClass.MIN_VERSION
                && mVersion < ConstantsClass.MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {
            mOuterConstraintLayout = null;
            mOuterRelativeLayout = findViewById(R.id.constraint_layout);
        } else {
            mOuterRelativeLayout = null;
            mOuterConstraintLayout = findViewById(R.id.constraint_layout);
        }

        mPosterImageIv = findViewById(R.id.poster);
        mTitleTv = findViewById(R.id.title);
        mReleaseDateTv = findViewById(R.id.release_date);
        mVoteAverageTv = findViewById(R.id.vote_average);
        mOverviewTv = findViewById(R.id.overview);
        mButtonAddMovie = findViewById(R.id.add_to_favorites);

        mVideosRC = findViewById(R.id.recyclerview_videos);
        mVideosDotsContainer = findViewById(R.id.videos_dots_container);
        mReviewsRC = findViewById(R.id.recyclerview_reviews);
        mReviewsDotsContainer = findViewById(R.id.reviews_dots_container);
        mVideosLabel = findViewById(R.id.videos_label);
        mReviewsLabel = findViewById(R.id.reviews_label);
        mRuntimeTv = findViewById(R.id.runtime);

        //Getting and setting the colors of the rating, release date and runtime icons
        ImageView starImage = findViewById(R.id.rating_image);
        starImage.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        ImageView runtimeImage = findViewById(R.id.runtime_image);
        runtimeImage.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        ImageView releaseDateImage = findViewById(R.id.release_image);
        releaseDateImage.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);

        //Setting the RecyclerViews
        LinearLayoutManager videosManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mVideosRC.setLayoutManager(videosManager);
        mVideosAdapter = new VideosAdapter(this, mListOfVideos, this);
        mVideosRC.setAdapter(mVideosAdapter);
        attachingSnapHelper(mVideosRC);

        LinearLayoutManager reviewsManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mReviewsRC.setLayoutManager(reviewsManager);
        mReviewsAdapter = new ReviewsAdapter(mListOfReviews, this);
        mReviewsRC.setAdapter(mReviewsAdapter);
        attachingSnapHelper(mReviewsRC);

        //Getting the data
        Intent fromMain = getIntent();
        Movie movieObject = (Movie) fromMain.getSerializableExtra(ConstantsClass.INTENT_EXTRA_MOVIE);
        gettingTheValues(movieObject);

        //By default, removing videos, reviews and runtime
        noVideos();
        noReviews();
        mRuntimeTv.setText(getString(R.string.runtime_unavailable));

        settingTheUI();
        loadDetailsExtra();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Saving the recycler's positions
        if (!mListOfVideos.isEmpty() || mListOfVideos != null) {
            outState.putParcelable(VIDEOS_RECYCLER_POSITION, mVideosRC.getLayoutManager()
                    .onSaveInstanceState());
        }
        if (mListOfReviews != null && !mListOfReviews.isEmpty()) {
            outState.putParcelable(REVIEWS_RECYCLER_POSITION, mReviewsRC.getLayoutManager()
                    .onSaveInstanceState());
        }

        //Saving the scroll position in details activity
        outState.putIntArray(SCROLL_POSITION,
                new int[]{mNestedScrollView.getScrollX(), mNestedScrollView.getScrollY()});
    }

    @Override
    protected void onResume() {
        super.onResume();

        settingTheConnectivityReceiver();

        mAppBarListener = new DetailsAppBarListener(this, mCollapsingToolbarLayout, mToolbar,
                mTitleInImageTv, mVersion, mTitle);
        mAppBarLayout.addOnOffsetChangedListener(mAppBarListener);

        if (mSavedInstanceState == null) {
            //For smooth scrolling:
            mVideosRC.setNestedScrollingEnabled(false);
            mReviewsRC.setNestedScrollingEnabled(false);
            //Adding focusability to the outer layout because the RecyclerViews get focused
            // on the first time we enter the app
            if (mOuterConstraintLayout != null) {
                mOuterConstraintLayout.setFocusableInTouchMode(true);
            } else {
                mOuterRelativeLayout.setFocusableInTouchMode(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(mAppBarListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mConnectivityReceiver);
    }

    private void gettingTheValues(Movie movieObject) {
        if (movieObject.getId() != null) {
            mMovieId = movieObject.getId();
        } else {
            mMovieId = 0;
        }

        mTitle = movieObject.getTitle();
        mPosterPath = movieObject.getPosterPath();
        mBackdropPath = movieObject.getBackdropPath();
        mReleaseDate = movieObject.getReleaseDate();
        mVoteAverage = movieObject.getVoteAverage();
        mOverview = movieObject.getOverview();

        if (movieObject.getRuntime() != 0) {
            mRuntime = movieObject.getRuntime();
        }
        if (movieObject.getVideosResults() != null) {
            mListOfVideos = movieObject.getVideosResults();
        }
        if (movieObject.getVideosResults() != null) {
            mListOfReviews = movieObject.getReviewsResults();
        }
    }

    private void settingTheUI() {
        //Setting the floating action button image,
        // depending on whether the movie is on the db or not
        getSupportLoaderManager().restartLoader(
                DB_LOADER_DETAILS_ID_QUERY_FOR_BUTTON, null, this);

        if (!TextUtils.isEmpty(mTitle)) {
            mBackgroundIv.setContentDescription(mTitle);
            mPosterImageIv.setContentDescription(mTitle);
            mTitleTv.setText(mTitle);
        }

        Drawable placeholder =
                ContextCompat.getDrawable(this, R.drawable.projector_placeholder);
        Drawable errorHorizontal = DisplayUtils.setErrorHorizontalPlaceholder(this);
        String movieBackdrop = mBackdropPath;
        if (!TextUtils.isEmpty(mBackdropPath)) {
            movieBackdrop = ApiUtils.uriImagesParser(
                    BACKDROP_SIZE, mBackdropPath.split("/")[1]);
        }
        PopularMoviesApp.picassoWithCache
                .load(movieBackdrop)
                .placeholder(placeholder)
                .error(errorHorizontal)
                .into(mBackgroundIv);

        Drawable errorVertical =
                ContextCompat.getDrawable(this, R.drawable.image_not_available_vertical);
        String moviePoster = ApiUtils.uriImagesParser(
                POSTER_SIZE, mPosterPath.split("/")[1]);

        //Setting the poster's width and height
        int imageWidth = DisplayUtils.getPosterImageWidth(this);
        int imageHeight = DisplayUtils.getPosterImageHeight(imageWidth);
        android.view.ViewGroup.LayoutParams layoutParams = mPosterImageIv.getLayoutParams();
        layoutParams.width = imageWidth;
        layoutParams.height = imageHeight;
        mPosterImageIv.setLayoutParams(layoutParams);

        PopularMoviesApp.picassoWithCache
                .load(moviePoster)
                .placeholder(placeholder)
                .error(errorVertical)
                .into(mPosterImageIv);

        if (!TextUtils.isEmpty(mReleaseDate)) {
            String year = mReleaseDate.split("-")[0];
            mReleaseDateTv.setText(year);
        }

        mVoteAverageTv.setText(getString(R.string.movie_rating, mVoteAverage));

        if (!TextUtils.isEmpty(mOverview)) {
            mOverviewTv.setText(mOverview);
        }

        if (mRuntime != 0) {
            mRuntimeTv.setText(getString(R.string.runtime_string, mRuntime));
        }

        if (mListOfVideos != null && !mListOfVideos.isEmpty()) {
            settingVideosUI();
        }
        if (mListOfReviews != null && !mListOfReviews.isEmpty()) {
            settingReviewsUI();
        }

        if (mVersion >= ConstantsClass.MIN_VERSION
                && mVersion < ConstantsClass.MIN_VERSION_WITH_CONSTRAINT_LAYOUT) {

            mTitleInImageTv = findViewById(R.id.title_in_image);
            if (!TextUtils.isEmpty(mTitle)) {
                mTitleInImageTv.setText(mTitle);
            }
            mCollapsingToolbarLayout.setTitle(" ");
        }
    }

    private void loadDetailsExtra() {
        Log.d(TAG_DETAILS, mMovieApiService
                .getMovieDetails(mMovieId, ConstantsClass.API_KEY, APPEND_TO_RESPONSE)
                .request().url().toString());

        Call<MovieDetails> call = mMovieApiService
                .getMovieDetails(mMovieId, ConstantsClass.API_KEY, APPEND_TO_RESPONSE);
        call.enqueue(new Callback<MovieDetails>() {

            @Override
            public void onResponse(@NonNull Call<MovieDetails> call,
                                   @NonNull Response<MovieDetails> response) {

                if (response.isSuccessful()) {
                    MovieDetails movieDetails = response.body();

                    if (mRuntime == 0) {
                        if (movieDetails.getRuntime() == null || movieDetails.getRuntime() == 0) {
                            mRuntimeTv.setText(getString(R.string.runtime_unavailable));
                        } else {
                            mRuntime = movieDetails.getRuntime();
                            mRuntimeTv.setText(getString(R.string.runtime_string, mRuntime));
                        }
                    }

                    if (mListOfVideos.isEmpty() || mListOfVideos == null) {
                        mListOfVideos = movieDetails.getVideos().getResults();
                        if (mListOfVideos.isEmpty() || mListOfVideos == null) {
                            noVideos();
                        } else {
                            settingVideosUI();
                        }
                    }

                    if (mListOfReviews == null || mListOfReviews.isEmpty()) {
                        mListOfReviews = movieDetails.getReviews().getResults();
                        if (mListOfReviews.isEmpty() || mListOfReviews == null) {
                            noReviews();
                        } else {
                            settingReviewsUI();
                        }
                    }

                    if (mSavedInstanceState != null) {
                        if (mSavedInstanceState.containsKey(VIDEOS_RECYCLER_POSITION)) {
                            Parcelable recyclerVideosState = mSavedInstanceState
                                    .getParcelable(VIDEOS_RECYCLER_POSITION);
                            mVideosRC.getLayoutManager().onRestoreInstanceState(recyclerVideosState);
                        }
                        if (mSavedInstanceState.containsKey(REVIEWS_RECYCLER_POSITION)) {
                            Parcelable reviewsVideosState = mSavedInstanceState
                                    .getParcelable(REVIEWS_RECYCLER_POSITION);
                            mReviewsRC.getLayoutManager().onRestoreInstanceState(reviewsVideosState);
                        }
                        if (mSavedInstanceState.containsKey(SCROLL_POSITION)) {
                            final int[] position = mSavedInstanceState.getIntArray(SCROLL_POSITION);

                            if (position != null) {
                                final int xPosition = position[0];
                                final int yPosition = position[1];
                                //Move to the last position
                                mNestedScrollView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNestedScrollView.fling(yPosition);
                                        mNestedScrollView.scrollTo(xPosition, yPosition);
                                    }
                                });
                            }
                        }
                    }
                    Log.d(TAG_DETAILS, "Extras for " + mTitle + " loaded from API");

                } else {
                    int statusCode = response.code();
                    Log.d(TAG_DETAILS, "Error loading from API: " + statusCode);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetails> call, Throwable t) {
                Log.d(TAG_DETAILS, "Error loading from API: " + t.getMessage());
            }
        });
    }

    private void attachingSnapHelper(RecyclerView recycler) {
        final PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recycler);
    }

    private void settingVideosUI() {
        mVideosAdapter.updateVideosList(mListOfVideos);
        mDotsCount = mListOfVideos.size();
        AddingDotsToDetails.settingTheDots(DetailActivity.this,
                mDotsCount, mVideosRC, mVideosDotsContainer);
        mVideosRC.setVisibility(View.VISIBLE);
        mVideosLabel.setVisibility(View.VISIBLE);
        mVideosDotsContainer.setVisibility(View.VISIBLE);
    }

    private void noVideos() {
        mVideosRC.setVisibility(View.GONE);
        mVideosLabel.setVisibility(View.GONE);
        mVideosDotsContainer.setVisibility(View.GONE);
    }

    private void settingReviewsUI() {
        mReviewsRC.setVisibility(View.VISIBLE);
        mReviewsLabel.setVisibility(View.VISIBLE);
        mReviewsDotsContainer.setVisibility(View.VISIBLE);
        mReviewsAdapter.updateReviewsList(mListOfReviews);
        mDotsCount = mListOfReviews.size();
        AddingDotsToDetails.settingTheDots(DetailActivity.this,
                mDotsCount, mReviewsRC, mReviewsDotsContainer);
    }

    private void noReviews() {
        mReviewsRC.setVisibility(View.GONE);
        mReviewsLabel.setVisibility(View.GONE);
        mReviewsDotsContainer.setVisibility(View.GONE);
    }

    public void onClickAddMovie(View v) {
        if (TextUtils.isEmpty(mTitle) || (mMovieId == 0)) {
            return;
        }
        getSupportLoaderManager().restartLoader(
                DB_LOADER_DETAILS_ID_QUERY_FOR_ADDING, null, this);
    }

    @Override
    public void onVideoClick(View view, VideosResults videoResult) {
        Uri openVideoUri = Uri.parse(ConstantsClass.BASE_YOUTUBE_URL).buildUpon()
                .appendPath(ConstantsClass.PATH_WATCH)
                .appendQueryParameter(ConstantsClass.QUERY_VIDEO, videoResult.getKey())
                .build();

        if (view.getClass() == FloatingActionButton.class) {
            Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(videoResult.getName() + ": " + openVideoUri.toString())
                    .getIntent();
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(shareIntent);

        } else {
            Intent seeVideo = new Intent(Intent.ACTION_VIEW, openVideoUri);
            startActivity(seeVideo);
        }
    }

    @Override
    public void onReviewClick(ReviewsResults review) {
        Intent seeReview = new Intent(DetailActivity.this, ReviewActivity.class);
        seeReview.putExtra(ConstantsClass.EXTRA_REVIEW, review);
        seeReview.putExtra(ConstantsClass.EXTRA_TITLE, mTitle);
        startActivity(seeReview);
    }

    private void settingTheConnectivityReceiver() {
        mConnectivityReceiver = new ConnectivityReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(mConnectivityReceiver, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Toast pleaseConnect = Toast.makeText(this,
                getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT);

        if (isConnected) {
            pleaseConnect.cancel();
            loadDetailsExtra();
        } else {
            pleaseConnect.show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        Uri currentMovieUri = MoviesEntry.CONTENT_URI.buildUpon().
                appendPath(mMovieId + "").build();

        return new QueryDbDetailsLoader(this, currentMovieUri);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DB_LOADER_DETAILS_ID_QUERY_FOR_ADDING) {

            if (cursor.moveToNext()) {
                mButtonAddMovie.setImageResource(R.drawable.ic_favorite_border_white_24dp);

                deleteMovie();
                cursor.close();

            } else {
                cursor.close();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesEntry.COLUMN_TITLE, mTitle);
                contentValues.put(MoviesEntry.COLUMN_ID, mMovieId);
                contentValues.put(MoviesEntry.COLUMN_POSTER, mPosterPath);
                contentValues.put(MoviesEntry.COLUMN_BACKDROP, mBackdropPath);
                contentValues.put(MoviesEntry.COLUMN_OVERVIEW, mOverview);
                contentValues.put(MoviesEntry.COLUMN_RATING, mVoteAverage);
                contentValues.put(MoviesEntry.COLUMN_RELEASE, mReleaseDate);
                contentValues.put(MoviesEntry.COLUMN_RUNTIME, mRuntime);

                insertMovie(contentValues);

                mButtonAddMovie.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
        } else {
            if (cursor.moveToNext()) {
                mButtonAddMovie.setImageResource(R.drawable.ic_favorite_white_24dp);
            } else {
                mButtonAddMovie.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }
            cursor.close();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG_DETAILS, "LOADER RESET");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertMovie(ContentValues contentValues) {
        Uri uriInserted = getContentResolver().insert(MoviesEntry.CONTENT_URI, contentValues);

        if (mListOfVideos != null && !mListOfVideos.isEmpty()) {
            for (VideosResults eachVideo : mListOfVideos) {

                ContentValues contentValuesVideos = new ContentValues();
                contentValuesVideos.put(VideosEntry.COLUMN_KEY, eachVideo.getKey());
                contentValuesVideos.put(VideosEntry.COLUMN_NAME, eachVideo.getName());
                contentValuesVideos.put(VideosEntry.COLUMN_MOVIE_ID, mMovieId);
                getContentResolver().insert(VideosEntry.CONTENT_VIDEOS_URI, contentValuesVideos);
            }
        }

        if (mListOfReviews != null && !mListOfReviews.isEmpty()) {
            for (ReviewsResults eachReview : mListOfReviews) {

                ContentValues mReviewsContentValues = new ContentValues();
                mReviewsContentValues.put(ReviewsEntry.COLUMN_AUTHOR, eachReview.getAuthor());
                mReviewsContentValues.put(ReviewsEntry.COLUMN_CONTENT, eachReview.getContent());
                mReviewsContentValues.put(ReviewsEntry.COLUMN_MOVIE_ID, mMovieId);
                getContentResolver().insert(ReviewsEntry.CONTENT_REVIEWS_URI, mReviewsContentValues);
            }
        }
        if (uriInserted != null) {
            Toast.makeText(this,
                    mTitle + getString(R.string.toast_added),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteMovie() {
        final Uri currentMovieUri = MoviesEntry.CONTENT_URI.buildUpon().
                appendPath(mMovieId + "").build();

        int rowsDeleted = getContentResolver().delete(currentMovieUri,
                null, null);

        if (rowsDeleted != 0) {
            Toast.makeText(this,
                    mTitle + getString(R.string.toast_removed),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
