package com.example.SpringBloggerAPI.comment;

import com.example.SpringBloggerAPI.post.Post;
import com.example.SpringBloggerAPI.post.PostRepository;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void markCommentsDeletedByUserId_marksAllUserCommentsAsDeleted() {
        // given
        User user = new User("User", "mail@mail.pl", "pass");
        userRepository.save(user);

        Post post = new Post("Post", "Content", user);
        postRepository.save(post);

        Comment comment1 = new Comment("Comment 1", user, post);
        Comment comment2 = new Comment("Comment 2", user, post);
        underTest.save(comment1);
        underTest.save(comment2);

        // when
        underTest.markCommentsDeletedByUserId(user.getId());

        entityManager.flush();
        entityManager.clear();

        // then
        List<Comment> result = underTest.findAll();
        assertThat(result).allMatch(Comment::isDeleted);
    }

    @Test
    void markCommentsDeletedByPostUserId_marksAllCommentsOnUsersPosts() {
        // given
        User postOwner = new User("Owner", "owner@mail.pl", "pass");
        userRepository.save(postOwner);

        User commenter = new User("Commenter", "c@mail.pl", "pass");
        userRepository.save(commenter);

        Post post1 = new Post("Post 1", "Content", postOwner);
        Post post2 = new Post("Post 2", "Content", postOwner);
        postRepository.save(post1);
        postRepository.save(post2);

        Comment comment1 = new Comment("Comment 1", commenter, post1);
        Comment comment2 = new Comment("Comment 2", commenter, post2);
        Comment comment3 = new Comment("Comment 3", postOwner, post2);
        underTest.saveAll(List.of(comment1, comment2, comment3));

        // when
        underTest.markCommentsDeletedByPostUserId(postOwner.getId());

        entityManager.flush();
        entityManager.clear();

        // then
        List<Comment> result = underTest.findAll();
        assertThat(result).allMatch(Comment::isDeleted);
    }
}

