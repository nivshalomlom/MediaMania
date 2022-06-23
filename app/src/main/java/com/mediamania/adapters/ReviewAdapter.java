package com.mediamania.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mediamania.R;
import com.mediamania.adapters.utility.GenericAdapter;
import com.mediamania.data.Constants;
import com.mediamania.data.User;
import com.mediamania.data.adapter_items.ReviewItem;
import com.mediamania.logic.Utility;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Locale;

public class ReviewAdapter extends GenericAdapter<ReviewItem> {

    private final LinkedList<User> users;

    public ReviewAdapter(LinkedList<User> users, LinkedList<ReviewItem> reviews) {
        super(R.layout.review_item, reviews);

        this.users = users;
    }

    @Override
    protected void setupView(View itemView, ReviewItem item, int position) {
        // Get views
        ImageView profilePic = itemView.findViewById(R.id.review_profile_image);
        TextView username = itemView.findViewById(R.id.review_username);
        TextView rating = itemView.findViewById(R.id.review_rating);
        TextView content = itemView.findViewById(R.id.review_content);

        // Fetch needed user
        User user = this.users.get(position);

        // Load data
        username.setText(user.getUsername());
        rating.setText(String.format(Locale.ENGLISH, Constants.RATING_TEMPLATE, item.getScore()));
        content.setText(item.getContent());
        Glide.with(itemView)
                .load(user.getImageUrl())
                .centerCrop()
                .placeholder(Utility.PROFILE_PLACE_HOLDER_RES)
                .into(profilePic);
    }

    /**
     * Adds a review to rhe adapter
     * @param item The review
     * @param posterUUID The poster UUID
     */
    public void add(ReviewItem item, String posterUUID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(posterUUID).get().addOnCompleteListener(fetchTask -> {
            if (fetchTask.isSuccessful()) {
                // Fetch user
                User user = fetchTask.getResult().toObject(User.class);
                int index = this.users.indexOf(user);

                // If already posted edit review in adapter
                if (index != -1)
                    this.update(index, item);
                else {
                    this.users.add(user);
                    this.add(item);
                }
            }
            else throw new RuntimeException("Error unknown user UUID!");
        });
    }

}
