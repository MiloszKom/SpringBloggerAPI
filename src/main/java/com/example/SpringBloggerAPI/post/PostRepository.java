package com.example.SpringBloggerAPI.post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Post findByPostId(int id);
}
