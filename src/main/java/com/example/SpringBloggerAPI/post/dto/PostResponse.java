package com.example.SpringBloggerAPI.post.dto;

import com.example.SpringBloggerAPI.comment.dto.CommentResponse;
import com.example.SpringBloggerAPI.user.dto.UserSummary;

import java.util.List;

public class PostResponse {
    private final int id;
    private final String title;
    private final String content;
    private final UserSummary user;
    private final List<CommentResponse> comments;

    public PostResponse(int id, String title, String content, UserSummary user, List<CommentResponse> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
        this.comments = comments;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public UserSummary getUser() { return user; }
    public List<CommentResponse> getComments() { return comments; }
}

