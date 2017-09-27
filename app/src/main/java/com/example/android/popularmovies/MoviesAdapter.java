package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.themoviedb.Movie;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Mahmoud on 8/3/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movie> mMoviesData;

    WeakReference<Context> contextRef;

    final private MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mImageView;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.iv_movie_image);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adaptorPosition = getAdapterPosition();
            Movie movie = mMoviesData.get(adaptorPosition);
            mClickHandler.onClick(movie);
        }

    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        contextRef = new WeakReference(viewGroup.getContext());
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(contextRef.get());
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        DisplayMetrics displayMetrics = contextRef.get().getResources().getDisplayMetrics();
        if (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            holder.itemView.getLayoutParams().width = Toolbar.LayoutParams.WRAP_CONTENT;
            holder.itemView.getLayoutParams().height = Toolbar.LayoutParams.MATCH_PARENT;
        }

        Movie movie = mMoviesData.get(position);
        Context context = holder.mImageView.getContext();
        Uri uri = Uri.parse(movie.posterUrl());
        Picasso.with(context).load(uri).placeholder(R.drawable.movie_placeholder).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesData) return 0;
        return mMoviesData.size();
    }

    public void setMoviesData(List<Movie> moviesData){
        mMoviesData = moviesData;
        notifyDataSetChanged();
    }
}
