package com.mediamania.logic;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.mediamania.R;

/**
 * A class for utility methods
 */
public class Utility {

    public static final int PLACE_HOLDER_RES = R.drawable.error_icon;
    public static final int PROFILE_PLACE_HOLDER_RES = R.drawable.default_profile_image;

    /**
     * Launches a given activity
     * @param ctx The context that called this method
     * @param destination The target activity
     * @param bundle Bundled data to pass to the new activity
     * @param finishSource True if the source activity should finish
     * @param <T> The target activity type
     */
    public static <T extends Activity> void startActivity(Context ctx, Class<T> destination, @Nullable Bundle bundle, boolean finishSource) {
        Intent intent = new Intent(ctx, destination);
        if (bundle != null)
            intent.putExtras(bundle);

        if (finishSource)
            ((Activity) ctx).finish();

        ctx.startActivity(intent);
    }

    /**
     * Transitions between fragments on a given activity
     * @param activity The activity context
     * @param frame_layout_id The fragment container id
     * @param fragment The fragment to load
     * @param <A> The class of the activity
     */
    public static <A extends AppCompatActivity> void switchFragments(A activity, int frame_layout_id, Fragment fragment) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction
                .replace(frame_layout_id, fragment)
                .commit();
    }

    /**
     * Opens and returns a loading popup
     * @param ctx The context that called this method
     * @return The created loading screen
     */
    public static Dialog openLoadingPopup(Context ctx) {
        Dialog loadingScreen = new Dialog(ctx, R.style.Theme_MediaMania_LoadingPopup);
        loadingScreen.setContentView(R.layout.loading_popup);
        loadingScreen.setCanceledOnTouchOutside(false);

        loadingScreen.show();
        return loadingScreen;
    }

}
