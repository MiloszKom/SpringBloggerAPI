package com.example.SpringBloggerAPI.exception.types;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
