package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.exception.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(service.getAllPosts());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post savePost(@Valid @RequestBody PostRequest postRequest) {
        return service.savePost(postRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> gettingPost(@PathVariable int id) {
        return ResponseEntity.ok(service.getPost(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable int id, @Valid @RequestBody PostRequest postRequest) {
        Post updatedPost = service.updatePost(id, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int id) {
        service.deletePost(id);
    }
}
