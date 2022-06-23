package com.mediamania.data.adapter_items;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MediaItem implements Serializable {

    private final String title;
    private final String imageUrl;
    private final int year;

    private final String summery;
    private final Map<String, ReviewItem> reviews;

    public MediaItem(String title, String imageUrl, int year, String summery, @Nullable Map<String, ReviewItem> reviews) {
        // Save date
        this.title = title;
        this.imageUrl = imageUrl;
        this.year = year;
        this.summery = summery;

        // Setup review map
        if (reviews != null)
            this.reviews = reviews;
        else
            this.reviews = new HashMap<>();
    }

    public MediaItem() {
        this.title = "";
        this.imageUrl = "";
        this.year = 0;
        this.summery = "";
        this.reviews = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() { return this.imageUrl; }

    public int getYear() {
        return year;
    }

    public String getSummery() {
        return summery;
    }

    public Map<String, ReviewItem> getReviews() {
        return reviews;
    }

    public double computeRating() {
        assert this.reviews != null;
        if (this.reviews.size() == 0)
            return 0;

        double sum = this.reviews.values().stream().mapToDouble(ReviewItem::getScore).sum();
        return sum / this.reviews.size();
    }

}
