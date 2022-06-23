package com.mediamania.data.adapter_items;

public class NewsItem {

    private final String title;
    private final String body;
    private final String imageUrl;

    /**
     * Creates a new news item
     * @param title The title of the item
     * @param body The content of the item
     * @param imageUrl The online url for the image of this item
     */
    public NewsItem(String title, String body, String imageUrl) {
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
    }

    private NewsItem() {
        this.title = "";
        this.body = "";
        this.imageUrl = "";
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
