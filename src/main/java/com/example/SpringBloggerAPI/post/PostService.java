package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.exception.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post savePost(PostRequest postRequest) {
        Post post = new Post(0, postRequest.getTitle(), postRequest.getContent());
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
        if (existingPost == null) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }

        existingPost.setTitle(postRequest.getTitle());
        existingPost.setContent(postRequest.getContent());
        return repository.save(existingPost);
    }

    public void deletePost(int id) {
        if (!repository.existsById(id)) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
