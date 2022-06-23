package com.mediamania.data.adapter_items;

public class PosterItem {

    private final String imageUrl;
    private final String title;

    /**
     * Creates a new poster item
     * @param imageUrl The online url of the poster image
     * @param title The title of the poster
     */
    public PosterItem(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    private PosterItem() {
        this.imageUrl = "";
        this.title = "";
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }
}
