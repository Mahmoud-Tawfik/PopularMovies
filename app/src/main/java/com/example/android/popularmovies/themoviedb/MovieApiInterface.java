package com.example.android.popularmovies.themoviedb;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Mahmoud on 9/13/17.
 */

public interface MovieApiInterface {
    @GET("movie/{sort}")
    Call<MovieDBResult> loadMovieList(@Path("sort") String sort, @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieDBResult> loadMovieDetails(@Path("movie_id") String movieId);

    @GET("movie/{movie_id}/videos")
    Call<MovieVideos> loadMovieVideos(@Path("movie_id") String movieId);

    @GET("movie/{movie_id}/reviews")
    Call<MovieReviews> loadMovieReviews(@Path("movie_id") String movieId);
}
