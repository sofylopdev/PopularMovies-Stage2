package com.sofialopes.android.popularmoviesapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sofialopes.android.popularmoviesapp.R;
import com.sofialopes.android.popularmoviesapp.utils.ConnectivityReceiver;
import com.sofialopes.android.popularmoviesapp.utils.ConstantsClass;
import com.sofialopes.android.popularmoviesapp.utils.DisplayUtils;
import com.sofialopes.android.popularmoviesapp.utils.main.PaginationScrollListener;
import com.sofialopes.android.popularmoviesapp.utils.main.SpacesItemDecoration;
import com.sofialopes.android.popularmoviesapp.adapters.MoviesAdapter;
import com.sofialopes.android.popularmoviesapp.adapters.SpinnerAdapter;
import com.sofialopes.android.popularmoviesapp.data.ApiUtils;
import com.sofialopes.android.popularmoviesapp.data.database.QueryDbMainLoader;
import com.sofialopes.android.popularmoviesapp.data.models.Movie;
import com.sofialopes.android.popularmoviesapp.data.models.MoviesResults;
import com.sofialopes.android.popularmoviesapp.data.remote.MovieApiServiceInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MoviesAdapter.AdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>,
        ConnectivityReceiver.ConnectivityReceiverListener {

    public static final String TAG_MAIN = MainActivity.class.getName();

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private TextView mErrorTextView;
    private RelativeLayout mErrorContainer;

    private MoviesAdapter mAdapter;
    private List<Movie> mMovieList = new ArrayList<>(0);

    public static final int DB_LOADER_ID = 10;
    private MovieApiServiceInterface mMovieApiService;

    private String mSortMoviesPrefsBy;
    private String mSharedPrefsKey;
    private String mPopularPref;
    private String mTopRatedPref;
    private String mFavoritesPref;
    private SharedPreferences mSharedPreferencesObj;

    private static final String SAVING_RECYCLER_POSITION = "RecyclerPosition";
    public static final String SAVING_PAGE_CURRENT = "Page";
    public static final String SAVING_MOVIE_LIST = "MovieList";
    private Parcelable mRcState;
    private Bundle mSavedInstanceState;

    private static final int PAGE_START = 1;
    private int mCurrentPage = PAGE_START;
    private boolean mIsLoading = false;
    private boolean mIsLastPage = false;

    private boolean mIsLoadingInOnResume = false;
    private ConnectivityReceiver mConnectivityReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mSavedInstanceState = savedInstanceState;

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        setSupportActionBar(toolbar);

        mSharedPrefsKey = getString(R.string.sort_by);
        mPopularPref = getString(R.string.popular_pref);
        mTopRatedPref = getString(R.string.top_rated_pref);
        mFavoritesPref = getString(R.string.favorites_pref);

        mErrorTextView = findViewById(R.id.error_message);
        mRecyclerView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progress_bar);
        mErrorContainer = findViewById(R.id.error_layout);

        mMovieApiService = ApiUtils.getMovieApiService();

        int noOfColumns = DisplayUtils.getNumberOfColumns();
        GridLayoutManager layoutManager = new GridLayoutManager(this, noOfColumns);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new MoviesAdapter(this, mMovieList, this);
        mRecyclerView.setAdapter(mAdapter);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.frame_spacing);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        mRecyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                if (!mSortMoviesPrefsBy.equals(mFavoritesPref)) {
                    if (mRecyclerView.computeVerticalScrollOffset() != 0) {
                        mIsLoading = true;
                        mCurrentPage += 1;
                        loadNextPage();
                    }
                }
            }

            @Override
            public boolean isLastPage() {
                return mIsLastPage;
            }

            @Override
            public boolean isLoading() {
                return mIsLoading;
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(SAVING_RECYCLER_POSITION, mRecyclerView.getLayoutManager()
                .onSaveInstanceState());
        outState.putInt(SAVING_PAGE_CURRENT, mCurrentPage);

        Serializable movieSerialized = (Serializable) mMovieList;
        outState.putSerializable(SAVING_MOVIE_LIST, movieSerialized);
    }

    @Override
    protected void onResume() {
        super.onResume();

        settingTheConnectivityReceiver();

        mSharedPreferencesObj = getPreferences(Context.MODE_PRIVATE);
        mSortMoviesPrefsBy = mSharedPreferencesObj.getString(mSharedPrefsKey, mPopularPref);
        Log.d(TAG_MAIN, "onResume, preference is: " + mSortMoviesPrefsBy);

        if (mSortMoviesPrefsBy.equals(mFavoritesPref)) {
            hideErrorMessage();
            getSupportLoaderManager().restartLoader(DB_LOADER_ID, null, this);
        } else if (ConnectivityReceiver.isNetworkAvailable(this)) {
            hideErrorMessage();
            mIsLoadingInOnResume = true;
            showProgressBar();
            checkingAndLoadingStates();
        } else {
            showErrorMessage(getString(R.string.no_internet_connection));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRcState = mRecyclerView.getLayoutManager()
                .onSaveInstanceState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(mConnectivityReceiver);
    }

    private void checkingAndLoadingStates() {

        if (mSavedInstanceState != null && mSavedInstanceState.containsKey(SAVING_RECYCLER_POSITION)) {
            Log.d(TAG_MAIN, "HAS saved instance");

            mCurrentPage = mSavedInstanceState.getInt(SAVING_PAGE_CURRENT);
            if (mMovieList == null || mMovieList.isEmpty()) {
                Log.d(TAG_MAIN, "list null or empty");
               // if(mSavedInstanceState.getSerializable(SAVING_MOVIE_LIST) instanceof List){
                    mMovieList = (List<Movie>) mSavedInstanceState.getSerializable(SAVING_MOVIE_LIST);
               // }

            }

            mAdapter.updateMovieList(mMovieList);

            checkingRecyclerState();

        } else {
            Log.d(TAG_MAIN, "NO saved instance");
            checkingRecyclerState();
        }
        showRecycler();
    }

    private void checkingRecyclerState() {
        //mRcState allows to go to the same scrolling position when entering MainActivity
        // when coming from DetailsActivity (due to launchMode="singleTop" in Manifest)
        if (mRcState == null) {
            Log.d(TAG_MAIN, "Rc state is null");

            if (mSavedInstanceState != null) {
                Parcelable recyclerState = mSavedInstanceState.getParcelable(SAVING_RECYCLER_POSITION);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
            } else {
                loadFirstPage();
            }

        } else {
            Log.d(TAG_MAIN, "Rc state not null");
            if (mMovieList != null && !mMovieList.isEmpty()) {
                mAdapter.updateMovieList(mMovieList);
            }
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mRcState);
            mRcState = null;
        }
    }

    public void loadFirstPage() {
        mCurrentPage = 1;
        mSortMoviesPrefsBy = mSharedPreferencesObj.getString(mSharedPrefsKey, mPopularPref);

        Log.d(TAG_MAIN, mMovieApiService.getMovies(
                mSortMoviesPrefsBy, ConstantsClass.API_KEY, mCurrentPage)
                .request().url().toString());

        Call<MoviesResults> call = mMovieApiService.getMovies(
                mSortMoviesPrefsBy, ConstantsClass.API_KEY, mCurrentPage);
        call.enqueue(new Callback<MoviesResults>() {

            @Override
            public void onResponse(Call<MoviesResults> call, Response<MoviesResults> response) {

                if (response.isSuccessful()) {
                    loadPages(response, true);

                } else {
                    int statusCode = response.code();
                    showErrorMessage(getString(R.string.error_network_or_json));
                    Log.d("MainActivity",
                            "Error loading 1st page from API: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<MoviesResults> call, Throwable t) {
                showErrorMessage(getString(R.string.error_network_or_json));
                Log.d("MainActivity",
                        "Failed loading 1st page from API: " + t.getMessage());
            }
        });
    }

    private void loadNextPage() {
        mSortMoviesPrefsBy = mSharedPreferencesObj.getString(mSharedPrefsKey, mPopularPref);

        Log.d(TAG_MAIN, mMovieApiService.getMovies(
                mSortMoviesPrefsBy, ConstantsClass.API_KEY, mCurrentPage)
                .request().url().toString());

        Call<MoviesResults> call = mMovieApiService.getMovies(
                mSortMoviesPrefsBy, ConstantsClass.API_KEY, mCurrentPage);
        call.enqueue(new Callback<MoviesResults>() {

            @Override
            public void onResponse(Call<MoviesResults> call, Response<MoviesResults> response) {

                if (response.isSuccessful()) {
                    loadPages(response, false);

                } else {
                    int statusCode = response.code();
                    showErrorMessage(getString(R.string.error_network_or_json));
                    Log.d("MainActivity",
                            "Error loading page " + mCurrentPage + "from API: " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<MoviesResults> call, Throwable t) {
                showErrorMessage(getString(R.string.error_network_or_json));
                Log.d("MainActivity",
                        "Failed loading page " + mCurrentPage + " from API: " + t.getMessage());
            }
        });
    }

    private void loadPages(Response<MoviesResults> response, boolean isFirst) {
        int totalPages = response.body().getTotalPages();
        mIsLoading = false;
        showRecycler();

        if (isFirst) {
            mMovieList = response.body().getResults();
            mAdapter.updateMovieList(mMovieList);
            mRecyclerView.smoothScrollToPosition(0);
        } else {
            mMovieList.addAll(response.body().getResults());
            mAdapter.updateMovieList(mMovieList);
        }

        Log.d("MainActivity", "Movies loaded from API");

        if (mCurrentPage >= totalPages) {
            mIsLastPage = true;
        }
        mIsLoadingInOnResume = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        SpinnerAdapter customAdapter = new SpinnerAdapter(getApplicationContext(),
                getResources().obtainTypedArray(R.array.spinner_list_icons_array),
                getResources().getStringArray(R.array.spinner_list_titles_array));
        spinner.setAdapter(customAdapter);

        int spinnerPosition;
        if (mSortMoviesPrefsBy.equals(mPopularPref)) {
            spinnerPosition = 0;
        } else if (mSortMoviesPrefsBy.equals(mTopRatedPref)) {
            spinnerPosition = 1;
        } else {
            spinnerPosition = 2;
        }
        spinner.setSelection(spinnerPosition);

        SpinnerInteractionListener listener = new SpinnerInteractionListener();
        spinner.setOnTouchListener(listener);
        spinner.setOnItemSelectedListener(listener);

        return true;
    }

    public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener,
            View.OnTouchListener {

        boolean userSelect = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //this is necessary because the spinner was being called without user action
            v.performClick();
            userSelect = true;
            return true;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (userSelect) {
                String selected = ((TextView) view.findViewById(R.id.text_spinner))
                        .getText().toString();

                mCurrentPage = 1;
                mRcState = null;
                SharedPreferences.Editor editor = mSharedPreferencesObj.edit();
                if (selected.equals(getString(R.string.favorites))) {
                    hideErrorMessage();
                    editor.putString(mSharedPrefsKey, mFavoritesPref);
                    editor.commit();

                    getSupportLoaderManager().restartLoader(DB_LOADER_ID,
                            null,
                            MainActivity.this);
                    userSelect = false;

                } else if (selected.equals(getString(R.string.popular))) {
                    editor.putString(mSharedPrefsKey, mPopularPref);
                    editor.commit();
                    if (ConnectivityReceiver.isNetworkAvailable(MainActivity.this)) {
                        hideErrorMessage();
                        loadFirstPage();

                    } else {
                        showErrorMessage(getString(R.string.no_internet_connection));
                    }
                    userSelect = false;
                } else if (selected.equals(getString(R.string.top_rated))) {
                    editor.putString(mSharedPrefsKey, mTopRatedPref);
                    editor.commit();
                    if (ConnectivityReceiver.isNetworkAvailable(MainActivity.this)) {
                        hideErrorMessage();
                        loadFirstPage();

                    } else {
                        showErrorMessage(getString(R.string.no_internet_connection));
                    }
                    userSelect = false;
                }
                mSortMoviesPrefsBy = mSharedPreferencesObj.getString(mSharedPrefsKey, mPopularPref);
                userSelect = false;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    @Override
    public void onMovieClick(View view, Movie data) {

        Intent goToDetails = new Intent(MainActivity.this, DetailActivity.class);
        goToDetails.putExtra(ConstantsClass.INTENT_EXTRA_MOVIE, data);

        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, getString(R.string.transition_name));

        startActivity(goToDetails, options.toBundle());
    }

    private void showProgressBar() {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void showRecycler() {
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage(String errorMessage) {
        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        mErrorTextView.setText(errorMessage);
        mErrorContainer.setVisibility(View.VISIBLE);
    }

    private void hideErrorMessage() {
        mErrorContainer.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        return new QueryDbMainLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> moviesList) {

        if (moviesList.isEmpty()) {
            showErrorMessage(getString(R.string.no_favorites));
            return;
        }
        mAdapter.updateMovieList(moviesList);
        showRecycler();

    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        mAdapter.updateMovieList(null);
    }

    private void settingTheConnectivityReceiver() {
        mConnectivityReceiver = new ConnectivityReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mConnectivityReceiver, filter);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

        Toast pleaseConnect = Toast.makeText(this,
                getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT);

        if (isConnected) {
            pleaseConnect.cancel();
            if (!mIsLoadingInOnResume) {
                hideErrorMessage();
                if (!mSortMoviesPrefsBy.equals(mFavoritesPref)) {
                    //loading movies data
                    showProgressBar();
                    checkingAndLoadingStates();
                }
            }
        } else {
            pleaseConnect.show();
        }
    }
}
