package com.mediamania.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mediamania.R;
import com.mediamania.adapters.utility.GenericAdapter;
import com.mediamania.data.adapter_items.PosterItem;
import com.mediamania.logic.Utility;

import java.util.Collection;

public class PosterAdapter extends GenericAdapter<PosterItem> {

    /**
     * Creates a new poster adapter
     * @param layout_res_id The resources id of the item layout
     * @param items The items
     */
    public PosterAdapter(int layout_res_id, Collection<PosterItem> items) {
        super(layout_res_id, items);
    }

    @Override
    protected void setupView(View itemView, PosterItem item, int position) {
        // Get views
        ImageView posterImage = itemView.findViewById(R.id.poster_image);
        TextView posterTitle = itemView.findViewById(R.id.poster_title);

        // Load data
        posterTitle.setText(item.getTitle());
        Glide.with(itemView)
            .load(item.getImageUrl())
            .centerCrop()
            .placeholder(Utility.PLACE_HOLDER_RES)
            .into(posterImage);
    }

}
