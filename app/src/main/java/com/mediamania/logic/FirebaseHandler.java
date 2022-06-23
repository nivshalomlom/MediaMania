package com.mediamania.logic;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.MapValue;
import com.mediamania.R;
import com.mediamania.data.User;
import com.mediamania.data.adapter_items.MediaItem;
import com.mediamania.data.adapter_items.NewsItem;
import com.mediamania.data.adapter_items.ReviewItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class FirebaseHandler {

    // Firebase API
    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    // Internal flags
    private static final Latch busy = new Latch(false);

    /**
     * Creates a user in firebase with the following details
     * @param email The user's email
     * @param username The user's username
     * @param password The user's password
     * @param profileImage The user's profile image, if exists
     * @param onCompleteListener A callback to call when the user creation is done, accepts the current user (null if none)
     */
    public static void createNewUser(String email, String username, String password, @Nullable Uri profileImage, @Nullable Consumer<FirebaseUser> onCompleteListener) {
        // Prime busy latch
        busy.setState(true);

        // Authenticate user with firebase
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {
                // Get user uid
                FirebaseUser currentUser = auth.getCurrentUser();
                assert currentUser != null;
                String uid = auth.getCurrentUser().getUid();

                // Upload userdata
                DocumentReference userDoc = FirebaseFirestore.getInstance().collection("users").document(uid);
                if (profileImage != null) {
                    // Upload profile image
                    StorageReference storageReference = storage.getReference("ProfileImages/" + uid);
                    storageReference.putFile(profileImage).addOnCompleteListener(uploadTask -> {
                        if (uploadTask.isSuccessful()) {
                            // Get profile image url for glide
                            storageReference.getDownloadUrl().addOnCompleteListener(urlTask -> {
                                if (urlTask.isSuccessful()) {
                                    // Save user details
                                    User user = new User(username, urlTask.getResult().toString(), false);
                                    userDoc.set(user);

                                    // Release work latch
                                    busy.setState(false);

                                    // Update listener
                                    if (onCompleteListener != null)
                                        onCompleteListener.accept(currentUser);
                                }
                                else throw new RuntimeException("Error uploading user data!");
                            });
                        }
                        else throw new RuntimeException("Error uploading user image!");
                    });
                }
                else {
                    // Save user details
                    User user = new User(username, null, false);
                    userDoc.set(user);

                    // Release work latch
                    busy.setState(false);

                    // Update listener
                    if (onCompleteListener != null)
                        onCompleteListener.accept(currentUser);
                }
            }
            else throw new RuntimeException("Error creating user!");
        });
    }

    /**
     * Logs in a user with the given credentials
     * @param email The user's email
     * @param password The user's password
     * @return The login task
     */
    public static Task<AuthResult> loginUser(String email, String password) { return auth.signInWithEmailAndPassword(email, password); }

    /**
     * Updates UI according to current user
     * @param drawerView The navigation drawer view
     * @param profileImage The profile image view
     * @param username The username text view
     * @param email The email text view
     * @param onCompleteListener Code to run on method finish
     */
    public static void firebaseDrawerSetup(View drawerView, ImageView profileImage, TextView username, TextView email, @Nullable Consumer<User> onCompleteListener) {
        // Get user state
        FirebaseUser authUser = auth.getCurrentUser();

        // If busy latch is primed wait for state change
        if (busy.getState()) {
            busy.setStateChangedListener(state -> {
                firebaseDrawerSetup(drawerView, profileImage, username, email, onCompleteListener);
                busy.setStateChangedListener(null);
            });
            return;
        }

        if (authUser != null) {
            // Setup database path
            FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
            DocumentReference userDocRef = databaseReference.collection("users").document(authUser.getUid());

            // Retrieve user details
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Make sure user is real
                    DocumentSnapshot userDoc = task.getResult();

                    if (userDoc.exists()) {
                        // Read user data
                        User user = userDoc.toObject(User.class);
                        assert user != null;

                        // Load user details into view
                        email.setText(authUser.getEmail());
                        username.setText(user.getUsername());

                        if (user.getImageUrl() != null)
                            Glide.with(drawerView)
                                .load(userDoc.get("imageUrl"))
                                .centerCrop()
                                .placeholder(Utility.PROFILE_PLACE_HOLDER_RES)
                                .into(profileImage);

                        // Notify listener
                        if (onCompleteListener != null)
                            onCompleteListener.accept(user);
                    }
                    else throw new RuntimeException("Error non existent user!");
                }
                else throw new RuntimeException("Error retrieving user details!");
            });
        }
        else {
            // No user logged in
            profileImage.setImageResource(R.drawable.default_profile_image);
            username.setText(R.string.guest_username);
            email.setText(R.string.guest_email);

            // Notify listener
            if (onCompleteListener != null)
                onCompleteListener.accept(null);
        }

    }

    /**
     * Reads a given collection from Firestore
     * @param collection The name of the collection
     * @param type The type of data in the collection
     * @param onCollectionRead A callback to run after read finish
     * @param <T> The type of data being read
     */
    public static <T> void getCollection(String collection, Class<T> type, Consumer<HashMap<String, T>> onCollectionRead) {
        // Get Firestore connection
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = databaseReference.collection(collection);

        // Fetch items
        HashMap<String, T> output = new HashMap<>();
        collectionRef.get().addOnCompleteListener(fetchTask -> {
            // if task successful read items
            if (fetchTask.isSuccessful())
                for (QueryDocumentSnapshot document : fetchTask.getResult()) {
                    T item = document.toObject(type);
                    output.put(document.getId(), item);
                }

            // Handle callback
            onCollectionRead.accept(output);
        });
    }

    /**
     * @param authStateChangedListener A method to run on auth change
     */
    public static void setAuthStateChangedListener(Consumer<FirebaseAuth> authStateChangedListener) { auth.addAuthStateListener(authStateChangedListener::accept); }

    /**
     * Signs out the current user
     */
    public static void signOutUser() {
        if (auth.getCurrentUser() == null)
            return;

        auth.signOut();
    }

    /**
     * @return Returns the current logged in user, null if none
     */
    public static FirebaseUser getCurrentUser() { return auth.getCurrentUser(); }

    /**
     * Uploads a media item to firebase
     * @param category The item category (tv / movie / game)
     * @param image The item image URI
     * @param title The item title
     * @param description The item description
     * @param year The item release year
     * @param onCompleteListener A method to run when the upload is finished
     */
    public static void uploadMedia(String category, Uri image, String title, String description, int year, Runnable onCompleteListener) {
        // Get Firestore connection
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = databaseReference.collection(category);

        // Generate random id's
        String imageUUID = UUID.randomUUID().toString();
        String mediaUUID = UUID.randomUUID().toString();

        // Upload image
        StorageReference storageReference = storage.getReference("MediaImages/" + imageUUID);
        storageReference.putFile(image).addOnCompleteListener(uploadTask -> {
            if (uploadTask.isSuccessful()) {
                storageReference.getDownloadUrl().addOnCompleteListener(fetchTask -> {
                    if (fetchTask.isSuccessful()) {
                        String imageUrl = fetchTask.getResult().toString();
                        MediaItem item = new MediaItem(title, imageUrl, year, description, new HashMap<>());
                        collectionRef.document(mediaUUID).set(item).addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful())
                                onCompleteListener.run();
                            else throw new RuntimeException("Error uploading media data!");
                        });
                    }
                    else throw new RuntimeException("Error fetching download url!");
                });
            }
            else throw new RuntimeException("Error uploading image!");
        });
    }

    /**
     * Upload a review for a media item
     * @param category The item category
     * @param mediaUUID The item uuid
     * @param review The user review
     * @return A asynchronous task of the upload, null if upload failed
     */
    public static Task<Void> addReviewToMedia(String category, String mediaUUID, ReviewItem review) {
        // Make sure user is logged in
        FirebaseUser user = auth.getCurrentUser();
        if (user == null)
            return null;

        // Get Firestore connection
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
        DocumentReference mediaRef = databaseReference.collection(category).document(mediaUUID);

        // Upload review
        return mediaRef.update("reviews." + user.getUid(), review);
    }

    /**
     * Upload a news item
     * @param title The news title
     * @param body The news body
     * @param image The news image
     * @param onCompleteListener A method to run when the upload is finished
     */
    public static void uploadNews(String title, String body, Uri image, Runnable onCompleteListener) {
        // Get Firestore connection
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = databaseReference.collection("news");

        // Generate random id's
        String imageUUID = UUID.randomUUID().toString();
        String newsUUID = UUID.randomUUID().toString();

        // Upload image
        StorageReference storageReference = storage.getReference("NewsImages/" + imageUUID);
        storageReference.putFile(image).addOnCompleteListener(uploadTask -> {
            if (uploadTask.isSuccessful()) {
                storageReference.getDownloadUrl().addOnCompleteListener(fetchTask -> {
                    if (fetchTask.isSuccessful()) {
                        NewsItem item = new NewsItem(title, body, fetchTask.getResult().toString());
                        collectionRef.document(newsUUID).set(item).addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful())
                                onCompleteListener.run();
                            else throw new RuntimeException("Error uploading news data!");
                        });
                    }
                    else throw new RuntimeException("Error fetching download url!");
                });
            }
            else throw new RuntimeException("Error uploading image!");
        });
    }

}
