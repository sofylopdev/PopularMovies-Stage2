package com.sofialopes.android.popularmoviesapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.sofialopes.android.popularmoviesapp.R;
import com.sofialopes.android.popularmoviesapp.data.models.ReviewsResults;

import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.EXTRA_REVIEW;
import static com.sofialopes.android.popularmoviesapp.utils.ConstantsClass.EXTRA_TITLE;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expanded_review);

        Intent getReview = getIntent();
        ReviewsResults results = (ReviewsResults) getReview.getSerializableExtra(EXTRA_REVIEW);
        String title = getReview.getStringExtra(EXTRA_TITLE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        TextView reviewTextView = findViewById(R.id.review_tv);
        TextView authorTextView = findViewById(R.id.author_review_tv);

        reviewTextView.setText(results.getContent());
        authorTextView.setText(results.getAuthor());
    }
}
