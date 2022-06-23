package com.mediamania.data.adapter_items;

import java.io.Serializable;

public class ReviewItem implements Serializable {

    private float score;
    private String content;

    public ReviewItem(float score, String content) {
        this.score = score;
        this.content = content;
    }

    public ReviewItem() {
        this.score = 0;
        this.content = "";
    }

    public float getScore() {
        return score;
    }

    public String getContent() {
        return content;
    }

}
