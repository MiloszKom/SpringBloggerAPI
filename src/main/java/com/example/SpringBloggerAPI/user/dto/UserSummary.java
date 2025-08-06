package com.example.SpringBloggerAPI.user.dto;

public class UserSummary {
    private final int id;
    private final String username;

    public UserSummary(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
}
