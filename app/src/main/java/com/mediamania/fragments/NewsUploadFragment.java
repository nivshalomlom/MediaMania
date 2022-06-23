package com.mediamania.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mediamania.R;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

public class NewsUploadFragment extends Fragment {

    private ActivityResultLauncher<Intent> chooserLauncher;
    private Uri newsUri = null;

    public NewsUploadFragment() { super(R.layout.news_upload_fragment); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);

        // Configure chooser
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.addCategory(Intent.CATEGORY_OPENABLE);
        chooser.setType("image/*");

        // Load views
        assert view != null;
        EditText title = view.findViewById(R.id.news_upload_title_edit_text);
        EditText summery = view.findViewById(R.id.news_upload_description_edit_text);

        // Handle image chooser
        ImageView image = view.findViewById(R.id.news_upload_image);
        image.setOnClickListener(event -> this.chooserLauncher.launch(chooser));

        // Handle upload
        Button upload = view.findViewById(R.id.news_upload_submit_button);
        upload.setOnClickListener(event -> {
            // Get inputs
            String titleText = title.getText().toString();
            String summeryText = summery.getText().toString();

            // Run tests
            boolean titleEmpty = titleText.isEmpty();
            boolean summeryEmpty = summeryText.isEmpty();
            boolean imageEmpty = this.newsUri == null;

            // Notify user
            if (titleEmpty)
                title.setError("Please enter a title!");

            if (summeryEmpty)
                summery.setError("Please enter a title!");

            if (imageEmpty)
                Toast.makeText(this.getContext(), "Please select a image", Toast.LENGTH_SHORT).show();

            if (titleEmpty || summeryEmpty || imageEmpty)
                return;

            // Upload news
            Dialog loading = Utility.openLoadingPopup(this.getContext());
            FirebaseHandler.uploadNews(titleText, summeryText, this.newsUri, () -> {
                // Clean inputs
                this.newsUri = null;
                title.setText("");
                summery.setText("");
                image.setImageResource(0);

                // Dismiss loading screen
                loading.dismiss();
            });
        });

        // Handle content retrieval
        this.chooserLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK)
                    throw new RuntimeException("Error choosing image!");

                Intent data = result.getData();

                assert data != null;
                Uri uri = data.getData();
                image.setImageURI(uri);
                this.newsUri = uri;
            }
        );

        return view;
    }

}
