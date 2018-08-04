package com.sofialopes.android.popularmoviesapp.data.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 *
 * Created by Sofia on 2/28/2018.
 */

public class Videos {
    @SerializedName("results")
    @Expose
    private List<VideosResults> results = null;

    public List<VideosResults> getResults() {
        return results;
    }

    public void setResults(List<VideosResults> results) {
        this.results = results;
    }
}
