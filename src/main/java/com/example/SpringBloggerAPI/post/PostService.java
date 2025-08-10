package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.post.dto.PostDetailsDTO;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
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

    public PostDetailsDTO savePost(PostRequest postRequest) {
        User user = authService.getCurrentUser();
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUser(user);

        Post newPost = repository.save(post);

        return mapPostToDto(newPost);
    }

    public List<PostDetailsDTO> getAllPosts() {
        List<Post> posts = repository.findAll();

        return posts.stream()
                .map(this::mapPostToDto)
                .toList();
    }

    public PostDetailsDTO getSinglePost(int id) {
        Post post =  getPost(id);
        return mapPostToDto(post);
    }

    public PostDetailsDTO updatePost(int id, PostRequest postRequest){
        Post post = getPost(id);
        User current = authService.getCurrentUser();
        if (post.getUser() == null || post.getUser().getId() != current.getId()) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());

        Post updatedPost = repository.save(post);

        return mapPostToDto(updatedPost);
    }

    public void deletePost(int id) {
        Post post = getPost(id);
        User currentUser = authService.getCurrentUser();

        boolean isAdmin = authService.isAdmin(currentUser);
        boolean isOwner =post.getUser() == null || post.getUser().getId() != currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }

        repository.delete(post);
    }

    public PostDetailsDTO mapPostToDto(Post post) {
        UserSummaryDTO userSummaryDTO = new UserSummaryDTO(post.getUser().getId(), post.getUser().getUsername());

        List<CommentDetailsDTO> comments = post.getComments().stream().map(comment -> {
            User commentUser = comment.getUser();
            UserSummaryDTO commentUserSummaryDTO = new UserSummaryDTO(commentUser.getId(), commentUser.getUsername());
            return new CommentDetailsDTO(comment.getId(), comment.getContent(), commentUserSummaryDTO);
        }).toList();

        return new PostDetailsDTO(post.getId(), post.getTitle(), post.getContent(), userSummaryDTO, comments);
    }
}
