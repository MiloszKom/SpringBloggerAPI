package com.example.SpringBloggerAPI.exception.types;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(int postId) {
        super("Post not found with id: " + postId);}
}
