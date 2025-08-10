package com.example.SpringBloggerAPI.comment;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.dto.CommentRequest;
import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.exception.types.CommentNotFoundException;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.post.Post;
import com.example.SpringBloggerAPI.post.PostService;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository repository;

    private final PostService postService;
    private final AuthService authService;

    public CommentService(CommentRepository repository, PostService postService, AuthService authService) {
        this.repository = repository;
        this.postService = postService;
        this.authService = authService;
    }

    private Comment getComment(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + id));
    }

    public CommentDetailsDTO createComment(int postId, CommentRequest commentRequest) {
        User user = authService.getCurrentUser();
        Post post = postService.getPost(postId);

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(user);
        comment.setPost(post);

        Comment newComment = repository.save(comment);

        return mapCommentToDto(newComment);
    }

    public List<CommentDetailsDTO> getCommentsByPost(int postId) {
        Post post = postService.getPost(postId);

        return post.getComments().stream()
                .map(this::mapCommentToDto)
                .toList();
    }

    public CommentDetailsDTO getSingleComment(int postId, int commentId) {
        Post post = postService.getPost(postId);
        Comment comment = getComment(commentId);

        if (comment.getPost().getId() != post.getId()) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }

        return mapCommentToDto(comment);
    }

    public CommentDetailsDTO updateComment(int postId, int commentId, CommentRequest commentRequest) {
        Post post = postService.getPost(postId);
        Comment comment = getComment(commentId);
        User currentUser = authService.getCurrentUser();

        if (comment.getPost().getId() != post.getId()) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }

        boolean isOwner = comment.getUser() != null && comment.getUser().getId() == currentUser.getId();

        if (!isOwner) {
            throw new PermissionDeniedException("You are not authorized to update this comment");
        }

        comment.setContent(commentRequest.getContent());
        Comment updated = repository.save(comment);
        return mapCommentToDto(updated);
    }

    public void deleteComment(int postId, int commentId) {
        Post post = postService.getPost(postId);
        Comment comment = getComment(commentId);
        User currentUser = authService.getCurrentUser();

        if (comment.getPost().getId() != post.getId()) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }

        boolean isAdmin = authService.isAdmin(currentUser);
        boolean isOwner = comment.getUser() != null && comment.getUser().getId() == currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new PermissionDeniedException("You are not authorized to delete this comment");
        }

        repository.delete(comment);
    }


    private CommentDetailsDTO mapCommentToDto(Comment comment) {
        User user = comment.getUser();
        UserSummaryDTO summary = new UserSummaryDTO(user.getId(), user.getUsername());
        return new CommentDetailsDTO(comment.getId(), comment.getContent(), summary);
    }
}
