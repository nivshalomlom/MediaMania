package com.mediamania.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mediamania.R;
import com.mediamania.adapters.utility.GenericAdapter;
import com.mediamania.data.Constants;
import com.mediamania.data.adapter_items.MediaItem;
import com.mediamania.logic.Utility;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

public class MediaAdapter extends GenericAdapter<MediaItem> {

    // Comparators
    public static final Comparator<MediaItem> RATING_ASC = Comparator.comparingDouble(MediaItem::computeRating);
    public static final Comparator<MediaItem> RATING_DSC = (item1, item2) -> -Double.compare(item1.computeRating(), item2.computeRating());
    public static final Comparator<MediaItem> YEAR_ASC = Comparator.comparingInt(MediaItem::getYear);
    public static final Comparator<MediaItem> YEAR_DSC = (item1, item2) -> -Integer.compare(item1.getYear(), item2.getYear());


    /**
     * Creates a new generic adapter
     * @param layout_res_id The resources id of the item layout
     * @param items The items
     */
    public MediaAdapter(int layout_res_id, Collection<MediaItem> items) {
        super(layout_res_id, items);
    }

    @Override
    protected void setupView(View itemView, MediaItem item, int position) {
        // Find views
        ImageView image = itemView.findViewById(R.id.media_item_image);
        TextView title = itemView.findViewById(R.id.media_item_title);
        TextView year = itemView.findViewById(R.id.media_item_year);
        TextView rating = itemView.findViewById(R.id.media_item_rating);

        // Load data
        title.setText(item.getTitle());
        year.setText(String.format(Locale.ENGLISH, Constants.YEAR_TEMPLATE, item.getYear()));
        rating.setText(String.format(Locale.ENGLISH, Constants.RATING_TEMPLATE, item.computeRating()));
        Glide.with(itemView)
            .load(item.getImageUrl())
            .centerCrop()
            .placeholder(Utility.PLACE_HOLDER_RES)
            .into(image);
    }

}
