package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository repository;
    private final AuthService authService;

    public PostService(PostRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    public Post savePost(PostRequest postRequest) {
        User user = authService.getCurrentUser();
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUser(user);

        return repository.save(post);
    }

    public List<Post> getAllPosts() {
        return repository.findAll();
    }

    public Post getPost(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }

    public Post updatePost(int id, PostRequest postRequest){
        Post existingPost = getPost(id);
        User current = authService.getCurrentUser();
        if (existingPost.getUser() == null || existingPost.getUser().getId() != current.getId()) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }

        existingPost.setTitle(postRequest.getTitle());
        existingPost.setContent(postRequest.getContent());
        return repository.save(existingPost);
    }

    public void deletePost(int id) {
        Post existingPost = getPost(id);
        User current = authService.getCurrentUser();
        if (existingPost.getUser() == null || existingPost.getUser().getId() != current.getId()) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }
        repository.delete(existingPost);
    }
}
