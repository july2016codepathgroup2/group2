package com.daryl.codepathproject2.models;

public class User {
    String name;
    int profilePictureUrl;

    public User(String name, int profilePictureUrl) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public int getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
