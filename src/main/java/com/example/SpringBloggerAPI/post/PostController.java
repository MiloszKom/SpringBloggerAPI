package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.post.dto.PostDetailsDTO;
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
    public ResponseEntity<List<PostDetailsDTO>> getAllPosts() {
        List<PostDetailsDTO> posts = service.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<PostDetailsDTO> savePost(@Valid @RequestBody PostRequest postRequest) {
        PostDetailsDTO response = service.savePost(postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> gettingPost(@PathVariable int id) {
        return ResponseEntity.ok(service.getSinglePost(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> updatePost(@PathVariable int id, @Valid @RequestBody PostRequest postRequest) {
        PostDetailsDTO updatedPost = service.updatePost(id, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable int id) {
        service.deletePost(id);
    }
}
