package com.example.SpringBloggerAPI.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findById(int id);

    List<Post> findByIsDeletedFalse();

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.isDeleted  = true WHERE p.user.id = :userId")
    void markPostsDeletedByUserId(@Param("userId") int userId);
}
