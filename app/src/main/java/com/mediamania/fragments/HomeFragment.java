package com.mediamania.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mediamania.R;
import com.mediamania.activities.NewsItemActivity;
import com.mediamania.adapters.NewsAdapter;
import com.mediamania.adapters.PosterAdapter;
import com.mediamania.adapters.utility.SmoothLinearLayoutManager;
import com.mediamania.data.adapter_items.NewsItem;
import com.mediamania.data.adapter_items.PosterItem;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

import java.util.concurrent.atomic.AtomicInteger;

public class HomeFragment extends Fragment {

    private static final long POSTER_SWITCH_DELAY = 5000;
    private static final int VIEW_TO_LOAD = 2;
    private final AtomicInteger viewsLoaded = new AtomicInteger(0);

    private Runnable onViewsLoaded = null;
    private int posterShown = 0;

    public HomeFragment() { super(R.layout.home_fragement); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);

        // Load views
        assert view != null;
        this.setupPosters(view);
        this.setupNewsFeed(view);

        return view;
    }

    /**
     * Registers a listener for when all views in this fragment finished loading
     * @param onViewsLoaded The method to run when all views in this fragment finished loading
     */
    public void setOnViewsLoaded(Runnable onViewsLoaded) {
        this.onViewsLoaded =  onViewsLoaded;

        if (this.viewsLoaded.get() == VIEW_TO_LOAD)
            this.onViewsLoaded.run();
    }

    /**
     * Resets internal variables
     */
    public void reset() {
        this.viewsLoaded.set(0);
        this.onViewsLoaded = null;
        this.posterShown = 0;
    }

    /**
     * A method to notify the fragment that a view has finished loading
     */
    private void onViewLoad() {
        int val = this.viewsLoaded.incrementAndGet();

        if (val == VIEW_TO_LOAD && this.onViewsLoaded != null)
            this.onViewsLoaded.run();
    }

    /**
     * Sets up the news feed in the fragment
     * @param view The main view
     */
    private void setupNewsFeed(View view) {
        // Setup recycler view
        RecyclerView newsFeed = view.findViewById(R.id.home_frag_news_feed);
        FirebaseHandler.getCollection("news", NewsItem.class, items -> {
            // Load data to adapter
            NewsAdapter adapter = new NewsAdapter(R.layout.news_item, items.values());
            newsFeed.setAdapter(adapter);

            // Register click listener
            adapter.setOnItemClickListener((item, position) -> {
                Bundle data = new Bundle();
                data.putString("imageUrl", item.getImageUrl());
                data.putString("title", item.getTitle());
                data.putString("body", item.getBody());
                Utility.startActivity(this.getContext(), NewsItemActivity.class, data, true);
            });

            // Handle callback
            this.onViewLoad();
        });
    }

    /**
     * Sets up the poster view in the fragment
     * @param view The main view
     */
    private void setupPosters(View view) {
        // Setup recycler view
        final RecyclerView posters = view.findViewById(R.id.home_frag_posters);
        posters.setLayoutManager(new SmoothLinearLayoutManager(view.getContext()));

        // Fetch data from Firebase
        FirebaseHandler.getCollection("posters", PosterItem.class, items -> {
            // Load data to adapter
            PosterAdapter adapter = new PosterAdapter(R.layout.poster_item, items.values());
            posters.setAdapter(adapter);

            // Block user scroll input
            posters.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) { return true; }
            });

            // Setup auto smooth scroll
            posters.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        updatePoster(posters, adapter.getItemCount());
                }
            });
            posters.postDelayed(() -> this.updatePoster(posters, adapter.getItemCount()), POSTER_SWITCH_DELAY);

            // Notify loading finished
            this.onViewLoad();
        });

        // Make sure poster is centered
        posters.smoothScrollToPosition(this.posterShown);
    }

    /**
     * Moves the poster feed to the next poster
     * @param posters The posters feed
     * @param numPosters The amount of posters in the feed
     */
    private void updatePoster(RecyclerView posters, int numPosters) {
        this.posterShown = (this.posterShown + 1) % numPosters;
        posters.postDelayed(() -> posters.smoothScrollToPosition(this.posterShown), POSTER_SWITCH_DELAY);
    }

}
