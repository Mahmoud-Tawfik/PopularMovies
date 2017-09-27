package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.themoviedb.Movie;
import com.example.android.popularmovies.themoviedb.MovieApiClient;
import com.example.android.popularmovies.themoviedb.MovieApiInterface;
import com.example.android.popularmovies.themoviedb.MovieReviews;
import com.example.android.popularmovies.themoviedb.MovieVideos;
import com.example.android.popularmovies.themoviedb.Video;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Mahmoud on 8/3/17.
 */

public class DetailActivity extends AppCompatActivity implements VideosAdapter.VideosAdapterOnClickHandler {
    private static final String TAG = DetailActivity.class.getName();

    @BindView(R.id.recyclerview_videos) RecyclerView mVideosRecyclerView;
    @BindView(R.id.recyclerview_reviews) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.tv_movie_title) TextView mMovieTitleDisplay;
    @BindView(R.id.iv_movie_poster) ImageView mMoviePosterDisplay;
    @BindView(R.id.tv_movie_release_date) TextView mMovieReleaseDateDisplay;
    @BindView(R.id.tv_movie_rating) TextView mMovieRatingDisplay;
    @BindView(R.id.tv_movie_overview) TextView mMovieOverviewDisplay;
    @BindView(R.id.fav_button) Button mFavButton;
    @BindView(R.id.tab_view) TabHost mTabHost;

    private Movie movie;

    private VideosAdapter mVideosAdapter;
    private ReviewsAdapter mReviewsAdapter;

    MovieApiInterface apiService = MovieApiClient.getClient().create(MovieApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);

        mMoviePosterDisplay.setMaxWidth(this.getResources().getDisplayMetrics().widthPixels);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                movie = new Gson().fromJson(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
                mMovieTitleDisplay.setText(movie.getTitle());
                mMovieReleaseDateDisplay.setText(movie.year());
                mMovieRatingDisplay.setText(String.format("%.1f/10", movie.getVoteAverage()));
                mMovieOverviewDisplay.setText(movie.getOverview());
                Uri uri = Uri.parse(movie.posterUrl());
                Picasso.with(this).load(uri).placeholder(R.drawable.movie_placeholder).into(mMoviePosterDisplay);
            }
        }

        mTabHost.setup();

        TabHost.TabSpec spec1 = mTabHost.newTabSpec("Tab One");
        spec1.setContent(R.id.recyclerview_videos);
        spec1.setIndicator("Videos");
        mTabHost.addTab(spec1);

        TabHost.TabSpec spec2 = mTabHost.newTabSpec("Tab Two");
        spec2.setContent(R.id.recyclerview_reviews);
        spec2.setIndicator("Reviews");
        mTabHost.addTab(spec2);

        updateFavButtonText(movieInFav(movie));
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean inFav = movieInFav(movie);
                updateFavButtonText(!inFav);
                if (inFav) {
                    removeMovieFromFav(movie);
                } else {
                    addMovieToFav(movie);
                }
            }
        });

        mVideosAdapter = new VideosAdapter(this);
        mVideosRecyclerView.setAdapter(mVideosAdapter);
        if (movie.videos != null) mVideosAdapter.setVideosData(movie.videos.getVideos());

        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        if (movie.reviews != null) mReviewsAdapter.setReviewsData(movie.reviews.getReviews());

        mVideosRecyclerView.clearFocus();
        mReviewsRecyclerView.clearFocus();

        loadMovieVideos();
        loadMovieReviews();
    }

    void updateFavButtonText(Boolean inFav) {
        if (inFav) {
            mFavButton.setBackgroundResource(R.drawable.ic_fav);
            } else {
            mFavButton.setBackgroundResource(R.drawable.ic_not_fav);
            }
    }

    Boolean movieInFav (Movie movie) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId().toString()).build();
        return getContentResolver().query(uri, null, null, null, null).moveToFirst();
    }

    void addMovieToFav(Movie movie) {
        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movie.toContentValue());
        if(uri != null) {
            Toast.makeText(getBaseContext(), getString(R.string.add_to_favorites_message), Toast.LENGTH_SHORT).show();
        }
    }

    void removeMovieFromFav(Movie movie) {
        Uri uri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movie.getId().toString()).build();
        getContentResolver().delete(uri, null, null);
        Toast.makeText(getBaseContext(), getString(R.string.remove_from_favorites_message), Toast.LENGTH_SHORT).show();
    }

    public void loadMovieVideos(){
        Call<MovieVideos> movieVideocall = apiService.loadMovieVideos(movie.getId().toString());
        movieVideocall.enqueue(new Callback<MovieVideos>() {
            @Override
            public void onResponse(Call<MovieVideos>call, Response<MovieVideos> response) {
                if(response.body() !=null) {
                    movie.videos = response.body();
                    mVideosAdapter.setVideosData(movie.videos.getVideos());
                } else {
                    Toast.makeText(getApplicationContext(), "Can't get any video!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Can't get any video!");
                }
            }

            @Override
            public void onFailure(Call<MovieVideos>call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onClick(Video video) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.videoUrl())));
    }

    public void loadMovieReviews(){
        Call<MovieReviews> movieReviewscall = apiService.loadMovieReviews(movie.getId().toString());
        movieReviewscall.enqueue(new Callback<MovieReviews>() {
            @Override
            public void onResponse(Call<MovieReviews>call, Response<MovieReviews> response) {
                if(response.body() !=null) {
                    movie.reviews = response.body();
                    mReviewsAdapter.setReviewsData(movie.reviews.getReviews());
                } else {
                    Toast.makeText(getApplicationContext(), "Can't get any review!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Can't get any review!");
                }
            }

            @Override
            public void onFailure(Call<MovieReviews>call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }


}
