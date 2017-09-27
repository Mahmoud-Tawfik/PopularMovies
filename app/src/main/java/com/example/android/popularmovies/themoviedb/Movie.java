package com.example.android.popularmovies.themoviedb;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.popularmovies.data.MovieContract;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Mahmoud on 8/16/17.
 */

public class Movie {
    private final String POSTER_BASE_URL= "http://image.tmdb.org/t/p/w342";
    public String posterUrl(){ return (POSTER_BASE_URL + posterPath); }

    public String year(){
        return releaseDate.substring(0,4);
    }

    public MovieVideos videos = null;
    public MovieReviews reviews = null;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("video")
    @Expose
    private Boolean video;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("genre_ids")
    @Expose
    private List<Integer> genreIds = null;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ContentValues toContentValue(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry._ID, getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, getVoteCount());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VIDEO, getVideo());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, getPopularity());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE, getOriginalLanguage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_GENRE_IDS, Joiner.on(',').join(getGenreIds() ));
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH, getBackdropPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ADULT, getAdult());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, getReleaseDate());

        return contentValues;
    }

    public Movie(Cursor dbCursor) {
        this.id = dbCursor.getInt(dbCursor.getColumnIndex(MovieContract.MovieEntry._ID));
        this.voteCount = dbCursor.getInt(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
        this.video = dbCursor.getInt(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VIDEO)) == 1;
        this.voteAverage = dbCursor.getDouble(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        this.title = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        this.popularity = dbCursor.getDouble(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY));
        this.posterPath = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
        this.originalLanguage = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_LANGUAGE));
        this.originalTitle = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        String genreIdsString = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_GENRE_IDS));
        this.genreIds = Lists.transform(Splitter.on(",").trimResults().splitToList(genreIdsString), new Function<String, Integer>() {
                    public Integer apply(@Nullable String input) { return Integer.parseInt(input);}
                });
        this.backdropPath = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACK_DROP_PATH));
        this.adult = dbCursor.getInt(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ADULT)) == 1;
        this.overview = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
        this.releaseDate = dbCursor.getString(dbCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
    }
}