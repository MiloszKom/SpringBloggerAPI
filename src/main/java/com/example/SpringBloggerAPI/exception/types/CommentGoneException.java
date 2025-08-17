package com.example.SpringBloggerAPI.exception.types;

public class CommentGoneException extends RuntimeException {
    public CommentGoneException(int commentId) {
        super("Comment with id " + commentId + " has been deleted");
    }
}
