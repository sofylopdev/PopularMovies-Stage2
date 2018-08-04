package com.sofialopes.android.popularmoviesapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.utils.DisplayUtils;
import com.sofialopes.android.popularmoviesapp.PopularMoviesApp;
import com.sofialopes.android.popularmoviesapp.R;
import com.sofialopes.android.popularmoviesapp.data.models.VideosResults;

import java.util.ArrayList;
import java.util.List;

import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.BASE_SCREENSHOT_YOUTUBE;
import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.PATH_SCREENSHOTS;
import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.PATH_SCREENSHOT_FULL_SIZE;

/**
 *
 * Created by Sofia on 3/4/2018.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    private Context mContext;
    private List<VideosResults> mVideosList = new ArrayList<>();
    private AdapterClickHandler mClickOnVideo;

    public VideosAdapter(Context context,
                         List<VideosResults> videosList,
                         AdapterClickHandler clickHandler) {
        this.mVideosList = videosList;
        this.mContext = context;
        this.mClickOnVideo = clickHandler;
    }

    public interface AdapterClickHandler {
        void onVideoClick(View v, VideosResults path);
    }

    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videos_item, parent, false);
        return new VideosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosAdapter.VideosViewHolder holder, int position) {

        VideosResults currentVideo = mVideosList.get(position);

        Uri imageThumb = Uri.parse(BASE_SCREENSHOT_YOUTUBE).buildUpon()
                .appendPath(PATH_SCREENSHOTS)
                .appendPath(currentVideo.getKey())
                .appendPath(PATH_SCREENSHOT_FULL_SIZE)
                .build();

        Drawable placeholder =
                ContextCompat.getDrawable(mContext, R.drawable.projector_placeholder);
        Drawable errorHorizontal = DisplayUtils.setErrorHorizontalPlaceholder(mContext);

        PopularMoviesApp.picassoWithCache
                .load(imageThumb.toString())
                .placeholder(placeholder)
                .error(errorHorizontal)
                .into(holder.videoThumb);

        String videoTitle = currentVideo.getName();
        if (!TextUtils.isEmpty(videoTitle)) {
            holder.videoTitle.setText(videoTitle);
            holder.videoThumb.setContentDescription(videoTitle);
        }
    }

    @Override
    public int getItemCount() {
        if (mVideosList.isEmpty()) {
            return 0;
        }
        return mVideosList.size();
    }

    public void updateVideosList(List<VideosResults> items) {
        mVideosList = items;
        notifyDataSetChanged();
    }

    public class VideosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView videoThumb;
        FloatingActionButton shareVideo;
        TextView videoTitle;

        public VideosViewHolder(View itemView) {
            super(itemView);
            videoThumb = itemView.findViewById(R.id.video_thumb);
            shareVideo = itemView.findViewById(R.id.share_button);
            videoTitle = itemView.findViewById(R.id.video_title);
            shareVideo.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickOnVideo.onVideoClick(view, mVideosList.get(getAdapterPosition()));
        }
    }
}
