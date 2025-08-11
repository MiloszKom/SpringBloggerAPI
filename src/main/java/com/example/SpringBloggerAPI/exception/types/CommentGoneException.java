package com.example.SpringBloggerAPI.exception.types;

public class CommentGoneException extends RuntimeException {
    public CommentGoneException(String message) {
        super(message);
    }
}
