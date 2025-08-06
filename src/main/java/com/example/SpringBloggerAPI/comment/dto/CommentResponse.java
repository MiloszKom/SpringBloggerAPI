package com.example.SpringBloggerAPI.comment.dto;

import com.example.SpringBloggerAPI.user.dto.UserSummary;

public class CommentResponse {
    private final int id;
    private final String content;
    private final UserSummary user;

    public CommentResponse(int id, String content, UserSummary user) {
        this.id = id;
        this.content = content;
        this.user = user;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public UserSummary getUser() { return user; }
}