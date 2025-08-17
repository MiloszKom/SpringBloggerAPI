package com.example.SpringBloggerAPI.user.deletion;

import com.example.SpringBloggerAPI.comment.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentDeletionHandler implements UserDeletionHandler {
    private final CommentRepository commentRepository;

    public CommentDeletionHandler(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void handleUserDeletion(int userId) {
        commentRepository.markCommentsDeletedByUserId(userId);
        commentRepository.markCommentsDeletedByPostUserId(userId);
    }
}
