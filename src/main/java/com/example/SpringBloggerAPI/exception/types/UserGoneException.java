package com.example.SpringBloggerAPI.exception.types;

public class UserGoneException extends RuntimeException {
    public UserGoneException(String message) {
        super(message);
    }
}
