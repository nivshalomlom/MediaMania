package com.mediamania.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mediamania.R;
import com.mediamania.logic.Utility;

public class NewsItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_item);

        // Wire up toolbar
        View toolbar = this.findViewById(R.id.news_item_activity_toolbar);
        toolbar.findViewById(R.id.back_toolbar_btn).setOnClickListener(event -> Utility.startActivity(this, MainActivity.class, null, true));

        // Get views
        ImageView image = this.findViewById(R.id.news_item_activity_image);
        TextView title = this.findViewById(R.id.news_item_activity_title);
        TextView body = this.findViewById(R.id.news_item_activity_body);

        // Load news data
        Bundle data = getIntent().getExtras();
        title.setText(data.getString("title"));
        body.setText(data.getString("body"));
        Glide.with(image)
                .load(data.getString("imageUrl"))
                .centerCrop()
                .placeholder(Utility.PLACE_HOLDER_RES)
                .into(image);
    }
}