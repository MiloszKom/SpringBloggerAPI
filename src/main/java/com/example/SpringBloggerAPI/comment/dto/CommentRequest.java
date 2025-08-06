package com.example.SpringBloggerAPI.comment.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {
    @NotBlank(message = "Content of the comment must not be empty")
    private String content;

    public String getContent() {
        return content;
    }
}
