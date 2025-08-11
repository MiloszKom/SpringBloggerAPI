package com.example.SpringBloggerAPI.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<Comment> findById(int id);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.isDeleted  = true WHERE c.user.id = :userId")
    void markCommentsDeletedByUserId(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("UPDATE Comment c SET c.isDeleted  = true WHERE c.post.id IN (SELECT p.id FROM Post p WHERE p.user.id = :userId)")
    void markCommentsDeletedByPostUserId(@Param("userId") int userId);
}
