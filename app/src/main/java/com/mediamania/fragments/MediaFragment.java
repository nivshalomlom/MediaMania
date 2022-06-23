package com.mediamania.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mediamania.R;
import com.mediamania.activities.MediaDetails;
import com.mediamania.adapters.MediaAdapter;
import com.mediamania.data.adapter_items.MediaItem;
import com.mediamania.data.adapter_items.ReviewItem;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

public class MediaFragment extends Fragment {

    private static final HashMap<String, Comparator<MediaItem>> SORTING_OPTIONS = new HashMap<String, Comparator<MediaItem>>() {{
        put("Sort By", null);
        put("Year (ASC)", MediaAdapter.YEAR_ASC);
        put("Year (DSC)", MediaAdapter.YEAR_DSC);
        put("Rating (ASC)", MediaAdapter.RATING_ASC);
        put("Rating (DSC)", MediaAdapter.RATING_DSC);
    }};

    private final String collection;

    /**
     * Creates a new media fragment
     * @param collection The name of the data collection in Firestore
     */
    public MediaFragment(String collection) {
        super(R.layout.media_fragment);
        this.collection = collection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Get views
        assert view != null;
        RecyclerView mediaRecycler = view.findViewById(R.id.media_fragment_recycler);
        EditText searchInput = view.findViewById(R.id.media_fragment_search_input);
        ImageButton settingsBtn = view.findViewById(R.id.media_fragment_search_settings_button);

        // Get media from database
        FirebaseHandler.getCollection(this.collection, MediaItem.class, items -> {
            // Extract items data
            LinkedList<String> keys = new LinkedList<>();
            LinkedList<MediaItem> values = new LinkedList<>();

            items.forEach((key, value) -> {
                keys.add(key);
                values.add(value);
            });

            // Setup adapter
            MediaAdapter mediaAdapter = new MediaAdapter(R.layout.media_item, values);
            mediaRecycler.setAdapter(mediaAdapter);

            // Handle item click
            mediaAdapter.setOnItemClickListener((item, position) -> {
                // Package needed data
                Bundle data = new Bundle();
                data.putString("UUID", keys.get(position));
                data.putString("collection", this.collection);
                data.putSerializable("item", item);

                // Go to activity
                Utility.startActivity(this.getContext(), MediaDetails.class, data, false);
            });

            // Setup search filter
            searchInput.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable editable) { mediaAdapter.filterBy(item -> item.getTitle().toLowerCase().startsWith(editable.toString().toLowerCase()), true); }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            });

            // Handle search settings
            settingsBtn.setOnClickListener(btnView -> this.searchSettings(mediaAdapter));
            mediaAdapter.sortBy(MediaAdapter.RATING_ASC);
        });

        return view;
    }

    /**
     * A method to handle the search settings popup
     * @param mediaAdapter The media list adapter
     */
    private void searchSettings(MediaAdapter mediaAdapter) {
        // Inflate popup
        Dialog popup = new Dialog(this.getContext());
        popup.setContentView(R.layout.media_list_settings_popup);

        // Get views
        Spinner dropdown = popup.findViewById(R.id.media_list_settings_popup_spinner);
        Button apply = popup.findViewById(R.id.media_list_settings_popup_close);
        Button close = popup.findViewById(R.id.media_list_settings_popup_apply);

        // Setup dropdown
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, SORTING_OPTIONS.keySet().toArray(new String[0]));
        dropdown.setAdapter(dropdownAdapter);

        // Setup views
        close.setOnClickListener(closeView -> popup.dismiss());
        apply.setOnClickListener(applyView -> {
            // Account for hint option
            Comparator<MediaItem> comparator = SORTING_OPTIONS.get(dropdownAdapter.getItem(dropdown.getSelectedItemPosition()));
            if (comparator != null)
                mediaAdapter.sortBy(comparator);

            // Close popup after sort
            popup.dismiss();
        });

        // Start popup
        popup.show();
    }

}
