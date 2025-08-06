package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.post.dto.PostResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = service.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<PostResponse> savePost(@Valid @RequestBody PostRequest postRequest) {
        PostResponse response = service.savePost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> gettingPost(@PathVariable int id) {
        return ResponseEntity.ok(service.getSinglePost(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable int id, @Valid @RequestBody PostRequest postRequest) {
        PostResponse updatedPost = service.updatePost(id, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int id) {
        service.deletePost(id);
    }
}
