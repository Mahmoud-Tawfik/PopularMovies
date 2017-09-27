package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.themoviedb.Movie;
import com.example.android.popularmovies.themoviedb.MovieApiClient;
import com.example.android.popularmovies.themoviedb.MovieApiInterface;
import com.example.android.popularmovies.themoviedb.MovieDBResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.StrictMath.max;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {
    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.recyclerview_movies) RecyclerView mRecyclerView;
    @BindView(R.id.tv_error_message_display) TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.tv_no_fav_message_display) TextView mNoFavMessageDisplay;

    private MoviesAdapter mMoviesAdapter;

    int currentPage = 0;
    List<Movie> allMovies = new ArrayList<>();
    MovieDBResult movieDBResult;
    Boolean loading = false;

    final static String POPULAR_PARAM = "popular";
    final static String TOP_RATED_PARAM = "top_rated";
    final static String FAV_PARAM = "favorites";
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

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);
        int dpWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);

        int itemCount = max(dpWidth / 200, 2);

        int orientation = GridLayoutManager.VERTICAL;

        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            itemCount = max(dpHeight / 300, 1);
            orientation = GridLayoutManager.HORIZONTAL;
        }

        final GridLayoutManager layoutManager = new GridLayoutManager(this, itemCount);
        layoutManager.setOrientation(orientation);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mMoviesAdapter.setMoviesData(allMovies);

        loadMovies(1);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!sortType.equals(FAV_PARAM)) {
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    if (allMovies.size() > 0 && (lastVisible + 5 >= allMovies.size()) && !loading) {
                        loadMovies(currentPage + 1);
                    }
                }
            }
        });

    }

    MovieApiInterface apiService = MovieApiClient.getClient().create(MovieApiInterface.class);

    public void loadMovies(int page){
        loading = true;
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mNoFavMessageDisplay.setVisibility(View.INVISIBLE);
        Call<MovieDBResult> loadMoviesCall = apiService.loadMovieList(sortType, page);
        loadMoviesCall.enqueue(new Callback<MovieDBResult>() {
            @Override
            public void onResponse(Call<MovieDBResult>call, Response<MovieDBResult> response) {
                if(response.body() !=null) {
                    showMovieDataView();
                    movieDBResult = response.body();
                    currentPage = movieDBResult.getPage();
                    if (allMovies == null){
                        allMovies = movieDBResult.getMovies();
                    } else {
                        allMovies.addAll(movieDBResult.getMovies());
                    }
                    mMoviesAdapter.setMoviesData(allMovies);
                } else {
                    showErrorMessage();
                }
                loading = false;
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<MovieDBResult>call, Throwable t) {
                showErrorMessage();
                loading = false;
                mLoadingIndicator.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sortType.equals(FAV_PARAM)){
            loadFav();
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
                    allMovies.clear();
                    currentPage = 0;
                    loadMovies(1);
                }
                break;
            case R.id.sort_top_rated:
                Toast.makeText(this, "Top Rated", Toast.LENGTH_SHORT)
                        .show();
                if (!sortType.equals(TOP_RATED_PARAM)){
                    sortType = TOP_RATED_PARAM;
                    allMovies.clear();
                    currentPage = 0;
                    loadMovies(1);
                }
                break;
            case R.id.sort_fav:
                Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT)
                        .show();
                if (!sortType.equals(FAV_PARAM)){
                    loadFav();
                }
                break;
            default:
                break;
        }

        return true;
    }

    void loadFav(){
        sortType = FAV_PARAM;
        allMovies.clear();
        currentPage = 0;
        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                allMovies.add(new Movie(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        mMoviesAdapter.setMoviesData(allMovies);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if(allMovies.size() == 0){
            showNoFavMessage();
        } else {
            showMovieDataView();
        }
    }
    private void showMovieDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mNoFavMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mNoFavMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showNoFavMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mNoFavMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        sortType = savedInstanceState.getString(SORT_TYPE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SORT_TYPE, sortType);
        super.onSaveInstanceState(outState);
    }
}