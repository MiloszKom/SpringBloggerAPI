package com.example.SpringBloggerAPI.exception.types;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
