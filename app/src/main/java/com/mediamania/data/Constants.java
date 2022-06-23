package com.mediamania.data;

import androidx.fragment.app.Fragment;
import com.mediamania.R;
import com.mediamania.data.adapter_items.MenuItem;
import com.mediamania.fragments.HomeFragment;
import com.mediamania.fragments.LoginFragment;
import com.mediamania.fragments.MediaFragment;
import com.mediamania.fragments.MediaUploadFragment;
import com.mediamania.fragments.NewsUploadFragment;

import java.util.ArrayList;
import java.util.LinkedList;

public class Constants {

    // Menu items
    public static final int MENU_LENGTH = 5;

    public static final ArrayList<MenuItem> MENU_ITEMS = new ArrayList<MenuItem>() {{
        add(new MenuItem("Home", R.drawable.home_icon));
        add(new MenuItem("Log In", R.drawable.login_icon));
        add(new MenuItem("Movies", R.drawable.movie_icon));
        add(new MenuItem("TV Shows", R.drawable.tv_icon));
        add(new MenuItem("Video Games", R.drawable.video_games_icon));
    }};

    public static final ArrayList<MenuItem> ADMIN_MENU_ITEMS = new ArrayList<MenuItem>() {{
        add(new MenuItem("Media Upload", R.drawable.media_upload_icon));
        add(new MenuItem("News Upload", R.drawable.news_upload_icon));
    }};

    // Menu item fragments
    public static final Fragment[] MENU_ITEM_FRAGMENTS = {
            new HomeFragment(),
            new LoginFragment(),
            new MediaFragment("movies"),
            new MediaFragment("tv_shows"),
            new MediaFragment("video_games"),
            new MediaUploadFragment(),
            new NewsUploadFragment()
    };

    // Regex
    public static String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    // Formats
    public static final String RATING_TEMPLATE = "%.1f/5.0";
    public static final String YEAR_TEMPLATE = "(%d)";

}
