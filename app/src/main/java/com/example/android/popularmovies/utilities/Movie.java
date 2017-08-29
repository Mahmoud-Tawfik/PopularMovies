package com.example.android.popularmovies.utilities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mahmoud on 8/16/17.
 */

public class Movie {
    private  @SerializedName("poster_path") String posterPath;
    public Boolean adult;
    public String overview;
    private  @SerializedName("release_date") String releaseDate;
    public @SerializedName("genre_ids") int[] genreIds;
    public int id;
    public @SerializedName("original_title") String originalTitle;
    public @SerializedName("original_language") String originalLanguage;
    public String title;
    public @SerializedName("backdrop_path") String backdropPath;
    public Double popularity;
    public @SerializedName("vote_count") int voteCount;
    public Boolean video;
    public @SerializedName("vote_average") Double voteAverage;

    private final String POSTER_BASE_URL= "http://image.tmdb.org/t/p/w342";
    public String posterUrl(){ return (POSTER_BASE_URL + posterPath); }

    public String year(){
        return releaseDate.substring(0,4);
    }

}