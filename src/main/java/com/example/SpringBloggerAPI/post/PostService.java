package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.PostGoneException;
import com.example.SpringBloggerAPI.exception.types.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.post.dto.PostDetailsDTO;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import com.example.SpringBloggerAPI.user.role.RoleService;
import com.example.SpringBloggerAPI.user.role.RoleType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final AuthService authService;

    public PostService(PostRepository postRepository, AuthService authService) {
        this.postRepository = postRepository;
        this.authService = authService;
    }

    public Post getPost(int postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.isDeleted()) {
            throw new PostGoneException(postId);
        }

        return post;
    }

    public PostDetailsDTO savePost(PostRequest postRequest) {
        User user = authService.getCurrentUser();
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUser(user);

        Post newPost = postRepository.save(post);

        return PostMapper.toPostDetailsDTO(newPost);
    }

    public List<PostDetailsDTO> getAllPosts() {
        List<Post> posts = postRepository.findByIsDeletedFalse();

        return posts.stream()
                .map(PostMapper::toPostDetailsDTO)
                .toList();
    }

    public PostDetailsDTO getSinglePost(int id) {
        Post post = getPost(id);
        return PostMapper.toPostDetailsDTO(post);
    }

    public PostDetailsDTO updatePost(int id, PostRequest postRequest){
        Post post = getPost(id);
        User current = authService.getCurrentUser();
        if (post.getUser() == null || post.getUser().getId() != current.getId()) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());

        Post updatedPost = postRepository.save(post);

        return PostMapper.toPostDetailsDTO(updatedPost);
    }

    public void deletePost(int id) {
        Post post = getPost(id);
        User currentUser = authService.getCurrentUser();

        boolean isAdmin = authService.isAdmin(currentUser);
        boolean isOwner = post.getUser().getId() == currentUser.getId();

        if (!isAdmin && !isOwner) {
            throw new PermissionDeniedException("You are not the owner of this post");
        }

        post.setDeleted(true);

        if (post.getComments() != null) {
            post.getComments().forEach(comment -> comment.setDeleted(true));
        }

        postRepository.save(post);
    }
}
