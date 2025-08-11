package com.example.SpringBloggerAPI.exception.types;

public class UserDeletedException extends RuntimeException {
    public UserDeletedException(String message) {
        super(message);
    }
}
