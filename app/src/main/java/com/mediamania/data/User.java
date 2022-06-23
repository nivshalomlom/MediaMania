package com.mediamania.data;

public class User {

    private final String username;
    private final String imageUrl;
    private final boolean admin;

    public User(String username, String imageUrl, boolean admin) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.admin = admin;
    }

    public User() {
        this.username = "";
        this.imageUrl = "";
        this.admin = false;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isAdmin() {
        return admin;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            User user = (User) o;

            if (!this.username.equals(user.getUsername()))
                return false;

            if (!this.imageUrl.equals(user.getImageUrl()))
                return false;

            return this.admin == user.admin;
        }
        else return false;
    }

}
