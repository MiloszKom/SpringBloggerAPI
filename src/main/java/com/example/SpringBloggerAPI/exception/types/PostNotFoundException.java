package com.example.SpringBloggerAPI.exception.types;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(String message) {
        super(message);
    }
}
