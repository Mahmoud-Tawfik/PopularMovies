package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.themoviedb.Video;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mahmoud on 9/14/17.
 */

class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {
    private List<Video> mVideos;

    final private VideosAdapterOnClickHandler mClickHandler;

    interface VideosAdapterOnClickHandler {
        void onClick(Video video);
    }

    VideosAdapter(VideosAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView mImageView;

        VideosAdapterViewHolder(View view) {
            super(view);
            mImageView = (ImageView) view.findViewById(R.id.iv_video_thumb);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adaptorPosition = getAdapterPosition();
            Video video = mVideos.get(adaptorPosition);
            mClickHandler.onClick(video);
        }

    }

    @Override
    public VideosAdapter.VideosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new VideosAdapter.VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideosAdapter.VideosAdapterViewHolder holder, int position) {
        Video video = mVideos.get(position);
        Context context = holder.mImageView.getContext();
        Uri uri = Uri.parse(video.thumbnailUrl());
        Picasso.with(context).load(uri).into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        if (null == mVideos) return 0;
        return mVideos.size();
    }

    void setVideosData(List<Video> videos){
        mVideos = videos;
        notifyDataSetChanged();
    }

}
