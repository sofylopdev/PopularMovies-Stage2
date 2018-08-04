package com.sofialopes.android.popularmoviesapp.data.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 *
 * Created by Sofia on 2/28/2018.
 */

public class Reviews {
    @SerializedName("page")
    @Expose
    private Integer page;
    @SerializedName("results")
    @Expose
    private List<ReviewsResults> results = null;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;


    public List<ReviewsResults> getResults() {
        return results;
    }

    public void setResults(List<ReviewsResults> results) {
        this.results = results;
    }

}
