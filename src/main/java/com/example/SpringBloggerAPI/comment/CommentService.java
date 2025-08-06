package com.example.SpringBloggerAPI.comment;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.dto.CommentRequest;
import com.example.SpringBloggerAPI.comment.dto.CommentResponse;
import com.example.SpringBloggerAPI.post.Post;
import com.example.SpringBloggerAPI.post.PostService;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.dto.UserSummary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

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

    private Comment getComment(int commentId) {
        return repository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));
    }

    public CommentResponse createComment(int postId, CommentRequest commentRequest) {
        User user = authService.getCurrentUser();
        Post post = postService.getPost(postId);

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setUser(user);
        comment.setPost(post);

        Comment newComment = repository.save(comment);

        return mapCommentToDto(newComment);
    }

    public List<CommentResponse> getCommentsByPost(int postId) {
        Post post = postService.getPost(postId);

        return post.getComments().stream()
                .map(this::mapCommentToDto)
                .toList();
    }

    public CommentResponse getSingleComment(int postId, int commentId) {
        Post post = postService.getPost(postId);
        Comment comment = getComment(commentId);

        if (comment.getPost().getId() != post.getId()) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }

        return mapCommentToDto(comment);
    }

    public CommentResponse updateComment(int postId, int commentId, CommentRequest commentRequest) {
        System.out.println("3");
        Post post = postService.getPost(postId);
        Comment comment = getComment(commentId);
        System.out.println("4");
        if (comment.getPost().getId() != post.getId()) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }
        System.out.println("5");
        comment.setContent(commentRequest.getContent());
        Comment updated = repository.save(comment);
        System.out.println("6");
        return mapCommentToDto(updated);
    }

    public void deleteComment(int postId, int commentId) {
        Post post = postService.getPost(postId);
        Comment comment = getComment(commentId);
        if (comment.getPost().getId() != post.getId()) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }

        repository.delete(comment);
    }

    private CommentResponse mapCommentToDto(Comment comment) {
        User user = comment.getUser();
        UserSummary summary = new UserSummary(user.getId(), user.getUsername());
        return new CommentResponse(comment.getId(), comment.getContent(), summary);
    }

}
