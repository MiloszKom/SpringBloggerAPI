package com.example.SpringBloggerAPI.comment;

import com.example.SpringBloggerAPI.comment.dto.CommentRequest;
import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDetailsDTO> createComment(@PathVariable int postId,
                                                           @Valid @RequestBody CommentRequest commentRequest) {
        CommentDetailsDTO response = commentService.createComment(postId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentDetailsDTO>> getCommentsByPost(@PathVariable int postId) {
        List<CommentDetailsDTO> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDetailsDTO> getComment(@PathVariable int postId, @PathVariable int commentId) {
        CommentDetailsDTO response = commentService.getSingleComment(postId, commentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDetailsDTO> updateComment(@PathVariable int postId,
                                                           @PathVariable int commentId,
                                                           @RequestBody CommentRequest commentRequest) {
        CommentDetailsDTO updated = commentService.updateComment(postId, commentId, commentRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int postId, @PathVariable int commentId) {
        commentService.deleteComment(postId, commentId);
        return ResponseEntity.noContent().build();
    }
}

