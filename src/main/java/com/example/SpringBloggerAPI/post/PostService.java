package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.dto.CommentResponse;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.post.dto.PostResponse;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.dto.UserSummary;
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

    public Post getPost(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }

    public PostResponse savePost(PostRequest postRequest) {
        User user = authService.getCurrentUser();
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUser(user);

        Post newPost = repository.save(post);

        return mapPostToDto(newPost);
    }

    public List<PostResponse> getAllPosts() {
        List<Post> posts = repository.findAll();

        return posts.stream()
                .map(this::mapPostToDto)
                .toList();
    }

    public PostResponse getSinglePost(int id) {
        Post post =  getPost(id);
        return mapPostToDto(post);
    }

    public PostResponse updatePost(int id, PostRequest postRequest){
        Post existingPost = getPost(id);
        User current = authService.getCurrentUser();
        if (existingPost.getUser() == null || existingPost.getUser().getId() != current.getId()) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }

        existingPost.setTitle(postRequest.getTitle());
        existingPost.setContent(postRequest.getContent());

        Post updatedPost = repository.save(existingPost);

        return mapPostToDto(updatedPost);
    }

    public void deletePost(int id) {
        Post existingPost = getPost(id);
        User current = authService.getCurrentUser();
        if (existingPost.getUser() == null || existingPost.getUser().getId() != current.getId()) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }
        repository.delete(existingPost);
    }

    public PostResponse mapPostToDto(Post post) {
        UserSummary userSummary = new UserSummary(post.getUser().getId(), post.getUser().getUsername());

        List<CommentResponse> comments = post.getComments().stream().map(comment -> {
            User commentUser = comment.getUser();
            UserSummary commentUserSummary = new UserSummary(commentUser.getId(), commentUser.getUsername());
            return new CommentResponse(comment.getId(), comment.getContent(), commentUserSummary);
        }).toList();

        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), userSummary, comments);
    }
}
