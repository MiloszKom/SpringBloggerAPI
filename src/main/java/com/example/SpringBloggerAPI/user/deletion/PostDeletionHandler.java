package com.example.SpringBloggerAPI.user.deletion;

import com.example.SpringBloggerAPI.post.PostRepository;

public class PostDeletionHandler implements UserDeletionHandler {
    private final PostRepository postRepository;

    public PostDeletionHandler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void handleUserDeletion(int userId) {
        postRepository.markPostsDeletedByUserId(userId);
    }
}
