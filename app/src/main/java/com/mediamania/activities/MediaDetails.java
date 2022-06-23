package com.mediamania.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.mediamania.R;
import com.mediamania.adapters.ReviewAdapter;
import com.mediamania.data.Constants;
import com.mediamania.data.User;
import com.mediamania.data.adapter_items.MediaItem;
import com.mediamania.data.adapter_items.ReviewItem;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MediaDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_details);

        // Fetch data from intent
        Intent intent = this.getIntent();
        Bundle data = intent.getExtras();

        String UUID = data.getString("UUID");
        String collection = data.getString("collection");
        MediaItem item = (MediaItem) data.getSerializable("item");

        // Load views
        View poster = this.findViewById(R.id.media_details_poster);
        View toolbar = this.findViewById(R.id.media_details_toolbar);

        RecyclerView reviewsRV = this.findViewById(R.id.media_details_review_recycler);

        TextView summery = this.findViewById(R.id.media_details_summery);
        TextView reviewsAVG = this.findViewById(R.id.media_details_average_rating);
        TextView title = poster.findViewById(R.id.poster_title);

        EditText reviewContent = this.findViewById(R.id.media_details_review_content);
        RatingBar reviewRating = this.findViewById(R.id.media_details_review_rating);
        Button submitReview = this.findViewById(R.id.signup_activity_submit_btn);

        ImageView image = poster.findViewById(R.id.poster_image);
        ImageButton back = toolbar.findViewById(R.id.back_toolbar_btn);

        // Load data into views
        title.setText(item.getTitle());
        summery.setText(item.getSummery());
        reviewsAVG.setText(String.format(Locale.ENGLISH, " " + Constants.RATING_TEMPLATE, item.computeRating()));
        Glide.with(poster)
                .load(item.getImageUrl())
                .centerCrop()
                .placeholder(Utility.PLACE_HOLDER_RES)
                .into(image);

        // Load reviews
        this.loadReviews(item, reviewsRV, adapter ->
                submitReview.setOnClickListener(view -> {
                    // Fetch data
                    String content = reviewContent.getText().toString();
                    float rating = reviewRating.getRating();

                    // Check validity
                    if (content.length() == 0) {
                        reviewContent.setError("Please enter a review content!");
                        return;
                    }

                    // Create review
                    ReviewItem review = new ReviewItem(rating, content);

                    // Make sure user is connected
                    FirebaseUser current = FirebaseHandler.getCurrentUser();
                    if (current == null) {
                        Toast.makeText(this, "Please sign in to post a review!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Update data
                    FirebaseHandler.addReviewToMedia(collection, UUID, review);
                    adapter.add(review, current.getUid());

                    // Clean inputs
                    reviewContent.setText("");
                    reviewRating.setRating(0);
                }
        ));

        // Add listeners
        back.setOnClickListener(view -> finish());
    }

    /**
     * Loads the media review data into a given recycler view
     * @param source The media source
     * @param target The recycler view target
     * @param onCompleteListener A method to run when the loading is completed
     */
    private void loadReviews(MediaItem source, RecyclerView target, Consumer<ReviewAdapter> onCompleteListener) {
        FirebaseHandler.getCollection("users", User.class, users -> {
            // Organize users and their reviews
            LinkedList<User> userList = new LinkedList<>();
            LinkedList<ReviewItem> reviewList = new LinkedList<>();

            // Fetch relevant users
            source.getReviews().forEach((ID, review) -> {
                User user = users.containsKey(ID) ? users.get(ID) : new User("deleted user", "", false);

                userList.add(user);
                reviewList.add(review);
            });

            // Create adapter
            ReviewAdapter adapter = new ReviewAdapter(userList, reviewList);
            target.setAdapter(adapter);

            // Call listener
            onCompleteListener.accept(adapter);
        });
    }

}