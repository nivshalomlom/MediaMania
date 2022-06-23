package com.mediamania.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.mediamania.R;
import com.mediamania.data.Constants;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

public class SignupActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> chooserLauncher;
    private Uri profileImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Toolbar setup
        View toolbar = this.findViewById(R.id.signup_toolbar);
        toolbar.findViewById(R.id.back_toolbar_btn).setOnClickListener(event -> finish());

        // Set toolbar title
        TextView title = toolbar.findViewById(R.id.back_toolbar_title);
        title.setText(R.string.sign_up_page_title);

        // Get views
        ImageView profileImage = this.findViewById(R.id.signup_activity_profile_image);
        EditText email = this.findViewById(R.id.signup_activity_email_input);
        EditText username = this.findViewById(R.id.signup_activity_username_input);
        EditText password = this.findViewById(R.id.signup_activity_password_input);
        Button submit = this.findViewById(R.id.signup_activity_submit_btn);

        // Setup
        this.imageChooserSetup(profileImage);
        submit.setOnClickListener(event -> {
            // Get user data
            String emailStr = email.getText().toString();
            String usernameStr = username.getText().toString();
            String passwordStr = password.getText().toString();

            // Validate input
            boolean emailInvalid = !emailStr.matches(Constants.EMAIL_REGEX);
            boolean usernameInvalid = usernameStr.length() == 0;
            boolean passwordInvalid = passwordStr.length() == 0;

            if (emailInvalid)
                email.setError("Please enter a valid email!");

            if (usernameInvalid)
                username.setError("Please enter a username");

            if (passwordInvalid)
                password.setError("Please enter a password!");

            // Open loading screen
            Dialog loadingScreen = Utility.openLoadingPopup(this);

            // Signup user
            FirebaseHandler.createNewUser(
                    email.getText().toString(),
                    username.getText().toString(),
                    password.getText().toString(),
                    this.profileImageUri,
                    user -> {
                        // Close loading screen
                        loadingScreen.dismiss();

                        // Go to home page
                        Utility.startActivity(this, MainActivity.class, null, true);
                    });
        });
    }

    /**
     * Sets up the image chooser
     * @param profileImage The image destination
     */
    public void imageChooserSetup(ImageView profileImage) {
        // Configure chooser
        Intent chooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        chooser.addCategory(Intent.CATEGORY_OPENABLE);
        chooser.setType("image/*");

        // Handle content retrieval
        this.chooserLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != Activity.RESULT_OK)
                        throw new RuntimeException("Error choosing image!");

                    Intent data = result.getData();

                    assert data != null;
                    Uri uri = data.getData();
                    profileImage.setImageURI(uri);
                    this.profileImageUri = uri;
                }
        );
        profileImage.setOnClickListener(event -> this.chooserLauncher.launch(chooser));
    }

}