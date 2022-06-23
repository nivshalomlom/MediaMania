package com.mediamania.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mediamania.R;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

public class MediaUploadFragment extends Fragment {

    private static final String[] MEDIA_CATEGORIES = {"category", "movies", "tv shows", "video games"};

    private ActivityResultLauncher<Intent> chooserLauncher;
    private Uri mediaUri = null;

    public MediaUploadFragment() { super(R.layout.media_upload_fragment); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  super.onCreateView(inflater, container, savedInstanceState);

        // Configure chooser
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.addCategory(Intent.CATEGORY_OPENABLE);
        chooser.setType("image/*");

        // Load views
        assert view != null;
        EditText title = view.findViewById(R.id.media_upload_title_edit_text);
        EditText summery = view.findViewById(R.id.media_upload_description_edit_text);
        EditText year = view.findViewById(R.id.media_upload_year_edit_text);

        // Handle image chooser
        ImageView image = view.findViewById(R.id.media_upload_image);
        image.setOnClickListener(event -> this.chooserLauncher.launch(chooser));

        // Spinner setup
        Spinner categoryDropdown = view.findViewById(R.id.media_upload_category_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, MEDIA_CATEGORIES);
        categoryDropdown.setAdapter(adapter);

        // Handle upload
        Button upload = view.findViewById(R.id.media_upload_submit_button);
        upload.setOnClickListener(event -> {
            // Get inputs
            int category = categoryDropdown.getSelectedItemPosition();
            String titleText = title.getText().toString();
            String summeryText = summery.getText().toString();
            String yearText = year.getText().toString();

            // Run tests
            boolean titleEmpty = titleText.isEmpty();
            boolean summeryEmpty = summeryText.isEmpty();
            boolean yearEmpty = yearText.isEmpty();
            boolean imageEmpty = this.mediaUri == null;
            boolean categoryEmpty = category == 0;

            // Notify user
            if (titleEmpty)
                title.setError("Please enter a title!");

            if (summeryEmpty)
                summery.setError("Please enter a title!");

            if (yearEmpty)
                year.setError("Please enter a year!");

            if (imageEmpty)
                Toast.makeText(this.getContext(), "Please select a image", Toast.LENGTH_SHORT).show();

            if (categoryEmpty)
                Toast.makeText(this.getContext(), "Please select a category", Toast.LENGTH_SHORT).show();

            if (titleEmpty || summeryEmpty || yearEmpty || imageEmpty || categoryEmpty)
                return;

            // Upload media
            Dialog loading = Utility.openLoadingPopup(this.getContext());
            FirebaseHandler.uploadMedia(MEDIA_CATEGORIES[categoryDropdown.getSelectedItemPosition()], this.mediaUri, title.getText().toString(), summery.getText().toString(), Integer.parseInt(year.getText().toString()),
                () -> {
                    // Clean inputs
                    this.mediaUri = null;
                    title.setText("");
                    summery.setText("");
                    year.setText("");
                    image.setImageResource(0);
                    categoryDropdown.setSelection(0, true);

                    // Close loading
                    loading.dismiss();
                }
            );
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
                this.mediaUri = uri;
            }
        );

        return view;
    }

}
