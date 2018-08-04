package com.sofialopes.android.popularmoviesapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.R;
import com.sofialopes.android.popularmoviesapp.data.models.ReviewsResults;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Sofia on 3/6/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {

    private List<ReviewsResults> mListReviews = new ArrayList<>();
    private AdapterClickHandler mClickOnReview;

    public ReviewsAdapter(List<ReviewsResults> listReviews, AdapterClickHandler clicker) {
        this.mListReviews = listReviews;
        mClickOnReview = clicker;
    }

    public interface AdapterClickHandler {
        void onReviewClick(ReviewsResults review);
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.reviews_item, parent, false);

        return new ReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        String reviewText = mListReviews.get(position).getContent().trim() + "\n";
        String authorName = mListReviews.get(position).getAuthor();

        if (!TextUtils.isEmpty(reviewText)) {
            holder.review.setText(reviewText);
        }
        if (!TextUtils.isEmpty(authorName)) {
            holder.authorReview.setText(authorName);
        }
    }

    @Override
    public int getItemCount() {
        if (mListReviews.isEmpty()) {
            return 0;
        }
        return mListReviews.size();
    }

    public void updateReviewsList(List<ReviewsResults> items) {
        mListReviews = items;
        notifyDataSetChanged();
    }

    public class ReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView review;
        TextView authorReview;

        public ReviewsViewHolder(View itemView) {
            super(itemView);
            review = itemView.findViewById(R.id.review);
            authorReview = itemView.findViewById(R.id.author_review);
            review.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickOnReview.onReviewClick(mListReviews.get(getAdapterPosition()));
        }
    }
}
