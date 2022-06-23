package com.mediamania.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mediamania.R;
import com.mediamania.data.Constants;
import com.mediamania.adapters.MenuAdapter;
import com.mediamania.fragments.HomeFragment;
import com.mediamania.logic.FirebaseHandler;
import com.mediamania.logic.Utility;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    private static final int FRAG_CONTAINER = R.id.main_fragment_container;
    private static WeakReference<AppCompatActivity> CURRENT_INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update activity reference
        CURRENT_INSTANCE = new WeakReference<>(this);

        // Get and prepare home fragment
        HomeFragment homeFragment = ((HomeFragment) Constants.MENU_ITEM_FRAGMENTS[0]);
        homeFragment.reset();

        // Setup splash screen fade
        homeFragment.setOnViewsLoaded(() -> {
            // Get resource data
            long shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

            // Get views
            View content = this.findViewById(R.id.main_activity_content_view);
            View splash = this.findViewById(R.id.main_activity_loading_view);

            // Animation setup
            content.setAlpha(0f);
            content.setVisibility(View.VISIBLE);

            // Start content fade in
            content.animate()
                    .alpha(1f)
                    .setDuration(shortAnimationDuration);

            // Start splash screen fade out
            splash.animate()
                    .alpha(0f)
                    .setDuration(shortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            splash.setVisibility(View.GONE);
                        }
                    });
        });

        // Load home fragment to read data from firebase
        Utility.switchFragments(this, FRAG_CONTAINER, Constants.MENU_ITEM_FRAGMENTS[0]);

        // Setup navigation UI
        View toolbar = findViewById(R.id.navigation_toolbar);
        this.drawerSetup(toolbar);
        this.optionsSetup(toolbar);
    }

    /**
     * Configures the profile IO in the drawer
     */
    private void drawerProfileSetup(TextView toolbar_title, MenuAdapter adapter) {
        // Update UI on auth change, if new login go to home page
        FirebaseHandler.setAuthStateChangedListener(auth -> {
            // Get drawer views
            View drawer = CURRENT_INSTANCE.get().findViewById(R.id.navigation_drawer_menu);
            ImageView profile_image = drawer.findViewById(R.id.drawer_profile_image);
            TextView username = findViewById(R.id.drawer_username_text);
            TextView email = findViewById(R.id.drawer_email_text);

            // Update profile UI
            FirebaseHandler.firebaseDrawerSetup(drawer, profile_image, username, email, user -> {
                // Check user details
                boolean is_admin = false;
                if (user != null) {
                    Utility.switchFragments(CURRENT_INSTANCE.get(), FRAG_CONTAINER, Constants.MENU_ITEM_FRAGMENTS[0]);
                    toolbar_title.setText(Constants.MENU_ITEMS.get(0).getText());
                    is_admin = user.isAdmin();
                }

                // Add / Hide admin options
                if (is_admin)
                    adapter.showAdminItems();
                else adapter.hideAdminItems();
            });

            // Update drawer menu
            if (auth.getCurrentUser() == null) {
                Constants.MENU_ITEMS.get(1).setText("Log In");
                Constants.MENU_ITEMS.get(1).setIcon_id(R.drawable.login_icon);
            }
            else {
                Constants.MENU_ITEMS.get(1).setText("Log Out");
                Constants.MENU_ITEMS.get(1).setIcon_id(R.drawable.logout_icon);
            }
            adapter.notifyItemChanged(1);
        });
    }

    /**
     * Setup for the navigation drawer
     * @param toolbar The navigation toolbar
     */
    private void drawerSetup(View toolbar) {
        // Get all needed views
        ImageButton drawer_btn = toolbar.findViewById(R.id.toolbar_drawer_btn);
        TextView toolbar_title = toolbar.findViewById(R.id.toolbar_title_txt);
        DrawerLayout drawerLayout = this.findViewById(R.id.navigation_drawer);
        View drawer = this.findViewById(R.id.navigation_drawer_menu);

        // Setup toolbar
        toolbar_title.setText(Constants.MENU_ITEMS.get(0).getText());
        drawer_btn.setOnClickListener(event -> drawerLayout.open());

        // Setup drawer menu
        RecyclerView menu = drawer.findViewById(R.id.drawer_menu);
        MenuAdapter adapter = new MenuAdapter(R.layout.menu_item, Constants.MENU_ITEMS, Constants.ADMIN_MENU_ITEMS);
        this.drawerProfileSetup(toolbar_title, adapter);

        // Handle menu clicks
        adapter.setOnItemClickListener((item, i) -> {
            if (i == 1 && FirebaseHandler.getCurrentUser() != null)
                FirebaseHandler.signOutUser();
            else {
                Utility.switchFragments(CURRENT_INSTANCE.get(), FRAG_CONTAINER, Constants.MENU_ITEM_FRAGMENTS[i]);
                toolbar_title.setText(item.getText());
            }
            drawerLayout.close();
        });

        // Load menu adapter
        menu.setAdapter(adapter);
    }

    /**
     * Setup for the options menu
     * @param toolbar The navigation toolbar
     */
    private void optionsSetup(View toolbar) {
        ImageButton options_btn = toolbar.findViewById(R.id.toolbar_options_btn);

        options_btn.setOnClickListener(event -> {
            // Inflate options
            ContextThemeWrapper ctx = new ContextThemeWrapper(CURRENT_INSTANCE.get(), R.style.Theme_MediaMania_PopupMenu);
            PopupMenu menu = new PopupMenu(ctx, options_btn);
            menu.getMenuInflater().inflate(R.menu.options_popup_menu, menu.getMenu());

            menu.setOnMenuItemClickListener(item -> {
                // Handle button clicks
                if (item.getItemId() == R.id.options_exit_item) {
                    finish();
                    System.exit(0);
                }

                if (item.getItemId() == R.id.options_settings_item) {
                    // Create intent
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);

                    // Attach data
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "INSERT LINK TO APP HERE");
                    sendIntent.putExtra(Intent.EXTRA_TITLE, "Invite people to join media mania");

                    // Show the Share sheet
                    startActivity(Intent.createChooser(sendIntent, null));
                }

                return true;
            });

            menu.show();
        });
    }

}