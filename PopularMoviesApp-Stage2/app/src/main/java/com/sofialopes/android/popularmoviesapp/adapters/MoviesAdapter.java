package com.sofialopes.android.popularmoviesapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.utils.DisplayUtils;
import com.sofialopes.android.popularmoviesapp.PopularMoviesApp;
import com.sofialopes.android.popularmoviesapp.R;
import com.sofialopes.android.popularmoviesapp.data.ApiUtils;
import com.sofialopes.android.popularmoviesapp.data.models.Movie;

import java.util.ArrayList;
import java.util.List;

import static com.sofialopes.android.popularmoviesapp.data.ApiUtils.POSTER_SIZE;

/**
 *
 * Created by Sofia on 2/22/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {

    private Context mContext;
    private List<Movie> mMovieList = new ArrayList<>();
    private AdapterOnClickHandler mClickOnMovie;

    public interface AdapterOnClickHandler {
        void onMovieClick(View view, Movie data);
    }


    public MoviesAdapter(Context ctx, List<Movie> list, AdapterOnClickHandler clickHandler) {
        this.mContext = ctx;
        this.mMovieList = list;
        this.mClickOnMovie = clickHandler;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {

        int imageWidth = DisplayUtils.getPosterImageWidth(mContext);
        int imageHeight = DisplayUtils.getPosterImageHeight(imageWidth);

        //Setting the width and height of the imageView
        android.view.ViewGroup.LayoutParams layoutParams = holder.frame.getLayoutParams();
        layoutParams.width = imageWidth;
        layoutParams.height = imageHeight;
        holder.container.setLayoutParams(layoutParams);

        Movie movieObject = mMovieList.get(position);
        String title = movieObject.getTitle();
        if (!TextUtils.isEmpty(title)) {
            holder.posterImage.setContentDescription(title);
            holder.titleTv.setText(title);
        }

        String rating = mContext.getString(R.string.movie_rating, movieObject.getVoteAverage());
        if (!TextUtils.isEmpty(rating)) {
            holder.ratingTv.setText(rating);
        }

        Drawable error =
                ContextCompat.getDrawable(mContext, R.drawable.image_not_available_vertical);
        Drawable placeholder =
                ContextCompat.getDrawable(mContext, R.drawable.projector_placeholder);

        String posterPath = movieObject.getPosterPath().split("/")[1];

        if (!TextUtils.isEmpty(posterPath)) {
            String imageUrl = ApiUtils.uriImagesParser(POSTER_SIZE, posterPath);
            PopularMoviesApp.picassoWithCache
                    .load(imageUrl)
                    .resize(imageWidth, imageHeight)
                    .placeholder(placeholder)
                    .error(error)
                    .into(holder.posterImage);
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null || mMovieList.isEmpty()) {
            return 0;
        }
        return mMovieList.size();
    }

    public void updateMovieList(List<Movie> items) {
        mMovieList = items;
        notifyDataSetChanged();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView posterImage;
        ImageView frame;
        FrameLayout container;
        TextView ratingTv;
        TextView titleTv;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.movie_poster);
            frame = itemView.findViewById(R.id.frame);
            container = itemView.findViewById(R.id.frame_container);
            ratingTv = itemView.findViewById(R.id.rating_main);
            titleTv = itemView.findViewById(R.id.title_main);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickOnMovie.onMovieClick(view, mMovieList.get(getAdapterPosition()));
        }
    }
}
