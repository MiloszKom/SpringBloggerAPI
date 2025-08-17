package com.example.SpringBloggerAPI.exception.types;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(int commentId) {
        super("Comment not found with id: " + commentId);
    }
}
