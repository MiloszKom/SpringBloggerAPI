package com.example.SpringBloggerAPI.exception.types;

public class PostGoneException extends RuntimeException {
    public PostGoneException(int postId) {
        super("Post with id " + postId + " has been deleted");
    }
}
