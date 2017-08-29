package com.example.android.popularmovies.utilities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mahmoud on 8/16/17.
 */

public class MovieDBResult{
    public int page;
    public @SerializedName("total_results") int totalResults;
    public @SerializedName("total_pages") int totalPages;
    public @SerializedName("results") Movie[] movies;
}