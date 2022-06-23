package com.mediamania.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mediamania.R;
import com.mediamania.adapters.utility.GenericAdapter;
import com.mediamania.data.adapter_items.MenuItem;

import java.util.Collection;
import java.util.LinkedList;

public class MenuAdapter extends GenericAdapter<MenuItem> {

    private final Collection<MenuItem> adminItems;
    private final int originalSize;

    /**
     * Creates a new menu adapter
     * @param layout_res_id The resources id of the item layout
     * @param items The items
     */
    public MenuAdapter(int layout_res_id, Collection<MenuItem> items, @Nullable Collection<MenuItem> adminItems) {
        super(layout_res_id, new LinkedList<>(items));

        this.adminItems = adminItems;
        this.originalSize = this.getItemCount();
    }

    @Override
    protected void setupView(View itemView, MenuItem item, int position) {
        // Find views
        TextView text = itemView.findViewById(R.id.navigation_drawer_menu_item_text);
        ImageView icon = itemView.findViewById(R.id.navigation_drawer_menu_item_icon);

        // Load data
        text.setText(item.getText());
        icon.setImageResource(item.getIconId());
    }

    /**
     * Shows the admin options
     */
    public void showAdminItems() {
        // Make sure action is valid
        if (this.adminItems == null || this.getItemCount() != this.originalSize)
            return;

        // Update list
        this.addAll(this.adminItems);
    }

    /**
     * Hides the admin options
     */
    public void hideAdminItems() {
        // Make sure action is valid
        if (this.adminItems == null || this.getItemCount() == this.originalSize)
            return;

        // Update list
        this.removeAll(this.adminItems);
    }

}
