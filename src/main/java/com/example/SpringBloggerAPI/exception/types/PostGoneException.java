package com.example.SpringBloggerAPI.exception.types;

public class PostGoneException extends RuntimeException {
    public PostGoneException(String message) {
        super(message);
    }
}
