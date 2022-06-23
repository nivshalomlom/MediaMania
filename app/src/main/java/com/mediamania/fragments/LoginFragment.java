package com.mediamania.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mediamania.R;
import com.mediamania.activities.SignupActivity;
import com.mediamania.data.Constants;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

public class LoginFragment extends Fragment {

    public LoginFragment() { super(R.layout.login_fragment); }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Get views
        assert view != null;
        EditText emailTextView = view.findViewById(R.id.login_fragment_email_input);
        EditText passwordTextView = view.findViewById(R.id.login_fragment_password_input);
        Button submit = view.findViewById(R.id.login_fragment_submit_button);

        // Setup views
        TextView signupLink = view.findViewById(R.id.login_fragment_signup_link_text);
        signupLink.setOnClickListener(event -> Utility.startActivity(this.getContext(), SignupActivity.class, null, false));

        // Handle input submission
        submit.setOnClickListener(event -> {
            // Get credentials
            String email = emailTextView.getText().toString();
            String password = passwordTextView.getText().toString();

            // Validate input
            boolean emailInvalid = !email.matches(Constants.EMAIL_REGEX);
            boolean passwordInvalid = password.length() == 0;

            if (emailInvalid)
                emailTextView.setError("Please enter a valid email!");

            if (passwordInvalid)
                passwordTextView.setError("Please enter a password!");

            if (emailInvalid || passwordInvalid)
                return;

            // Start loading screen
            Dialog loadingScreen = Utility.openLoadingPopup(this.getContext());

            // Login user
            FirebaseHandler.loginUser(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Clear inputs
                    emailTextView.setText("");
                    passwordTextView.setText("");
                }
                else Toast.makeText(this.getContext(), "Incorrect email or password!", Toast.LENGTH_LONG).show();

                // Close loading screen
                loadingScreen.dismiss();
            });
        });

        return view;
    }

}
