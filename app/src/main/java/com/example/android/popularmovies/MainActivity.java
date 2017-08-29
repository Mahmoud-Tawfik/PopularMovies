package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.utilities.Movie;
import com.example.android.popularmovies.utilities.MovieDBResult;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.google.gson.Gson;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler, LoaderCallbacks<MovieDBResult> {

    @BindView(R.id.recyclerview_movies) RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    private MoviesAdapter mMoviesAdapter;

    private static final int MOVIES_LOADER_ID = 0;

    int loaderId = MOVIES_LOADER_ID;
    LoaderCallbacks<MovieDBResult> callback;

    int currentPage = 0;
    int maxPage = 0;
    Movie[] movies;

    final static String POPULAR_PARAM = "popular";
    final static String TOP_RATED_PARAM = "top_rated";
    private String sortType = POPULAR_PARAM;
    final static String SORT_TYPE = "SORT_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            sortType = savedInstanceState.getString(SORT_TYPE);
        }

        int itemCount = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, itemCount);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mMoviesAdapter.setMoviesData(movies);

        callback = MainActivity.this;

        if (getSupportLoaderManager().getLoader(loaderId) == null) {
            getSupportLoaderManager().initLoader(loaderId, null, callback);
        } else {
            getSupportLoaderManager().restartLoader(loaderId, null, callback);
        }

    }

    @Override
    public void onClick(Movie movie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, new Gson().toJson(movie));
        startActivity(intentToStartDetailActivity);
    }


    @Override
    public Loader<MovieDBResult> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<MovieDBResult>(this) {

            MovieDBResult mMoviesData = null;

            /**
             * Subclasses of AsyncTaskLoader must implement this to take care of loading their data.
             */
            @Override
            protected void onStartLoading() {
                if (mMoviesData != null) {
                    deliverResult(mMoviesData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieDBResult loadInBackground() {

                URL moviesRequestUrl = NetworkUtils.moviesUrl(sortType, currentPage + 1);

                try {
                    String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);

                    Gson gson = new Gson();
                    MovieDBResult results = gson.fromJson(jsonMoviesResponse, MovieDBResult.class);

                    return results;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            /**
             * Sends the result of the load to the registered listener.
             *
             * @param data The result of the load
             */
            public void deliverResult(MovieDBResult data) {
                mMoviesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieDBResult> loader, MovieDBResult data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (null == data) {
            showErrorMessage();
        } else {
            showMovieDataView();
            currentPage = data.page;
            maxPage = data.totalPages;
            movies = data.movies;
            mMoviesAdapter.setMoviesData(movies);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<MovieDBResult> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_most_popular:
                Toast.makeText(this, "Most Popular", Toast.LENGTH_SHORT)
                        .show();
                if (!sortType.equals(POPULAR_PARAM)){
                    sortType = POPULAR_PARAM;
                    movies = null;
                    currentPage = 0;
                    getSupportLoaderManager().restartLoader(loaderId, null, callback);

                }
                break;
            case R.id.sort_top_rated:
                Toast.makeText(this, "Top Rated", Toast.LENGTH_SHORT)
                        .show();
                if (!sortType.equals(TOP_RATED_PARAM)){
                    sortType = TOP_RATED_PARAM;
                    movies = null;
                    currentPage = 0;
                    getSupportLoaderManager().restartLoader(loaderId, null, callback);
                }
                break;
            default:
                break;
        }

        return true;
    }

    private void invalidateData() {
        mMoviesAdapter.setMoviesData(null);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        sortType = savedInstanceState.getString(SORT_TYPE);
    }

    // invoked when the activity may be temporarily destroyed, save the instance state here
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_TYPE, sortType);

        super.onSaveInstanceState(outState);
    }

}