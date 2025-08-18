package com.example.SpringBloggerAPI.exception.types;

// test adding decorators here here
public class CommentGoneException extends RuntimeException {
    public CommentGoneException(int commentId) {
        super("Comment with id " + commentId + " has been deleted");
    }
}
