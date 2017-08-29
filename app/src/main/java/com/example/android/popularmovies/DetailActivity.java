package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.utilities.Movie;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


/**
 * Created by Mahmoud on 8/3/17.
 */

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitleDisplay;
    private ImageView mMoviePosterDisplay;
    private TextView mMovieReleaseDateDisplay;
    private TextView mMovieRatingDisplay;
    private TextView mMovieOverviewDisplay;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitleDisplay = (TextView) findViewById(R.id.tv_movie_title);
        mMoviePosterDisplay = (ImageView) findViewById(R.id.iv_movie_poster);
        mMovieReleaseDateDisplay = (TextView) findViewById(R.id.tv_movie_release_date);
        mMovieRatingDisplay = (TextView) findViewById(R.id.tv_movie_rating);
        mMovieOverviewDisplay = (TextView) findViewById(R.id.tv_movie_overview);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                movie = new Gson().fromJson(intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT), Movie.class);
                mMovieTitleDisplay.setText(movie.title);
                mMovieReleaseDateDisplay.setText(movie.year());
                mMovieRatingDisplay.setText(movie.voteAverage.toString() + "/10");
                mMovieOverviewDisplay.setText(movie.overview);
                Uri uri = Uri.parse(movie.posterUrl());
                Picasso.with(this).load(uri).into(mMoviePosterDisplay);
            }
        }

    }

}
