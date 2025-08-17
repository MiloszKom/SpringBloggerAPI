package com.example.SpringBloggerAPI.post.dto;

import jakarta.validation.constraints.NotBlank;

public class PostRequest {
    @NotBlank(message = "Title of the post must not be empty")
    private String title;

    @NotBlank(message = "Content of the post must not be empty")
    private String content;

    public PostRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
