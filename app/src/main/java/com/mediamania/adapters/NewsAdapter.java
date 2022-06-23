package com.mediamania.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mediamania.R;
import com.mediamania.adapters.utility.GenericAdapter;
import com.mediamania.data.adapter_items.NewsItem;
import com.mediamania.logic.Utility;

import java.util.Collection;

/**
 * Adapter for news items
 */
public class NewsAdapter extends GenericAdapter<NewsItem> {

    private static final int MAX_LETTERS_FOR_BODY = 35;

    /**
     * Creates a new news adapter
     * @param layout_res_id The resources id of the item layout
     * @param items The items
     */
    public NewsAdapter(int layout_res_id, Collection<NewsItem> items) {
        super(layout_res_id, items);
    }

    @Override
    protected void setupView(View itemView, NewsItem item, int position) {
        // Find views
        TextView title = itemView.findViewById(R.id.news_item_title);
        TextView body = itemView.findViewById(R.id.news_item_body);
        ImageView image = itemView.findViewById(R.id.news_item_image);

        // Read data
        String bodyStr = item.getBody();
        if (bodyStr.length() > MAX_LETTERS_FOR_BODY)
            bodyStr = bodyStr.substring(0, MAX_LETTERS_FOR_BODY).trim() + "...";

        // Load data
        title.setText(item.getTitle());
        body.setText(bodyStr);
        Glide.with(itemView)
            .load(item.getImageUrl())
            .centerCrop()
            .placeholder(Utility.PLACE_HOLDER_RES)
            .into(image);
    }

}
