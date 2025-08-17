package com.example.SpringBloggerAPI.post;

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
class PostRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository underTest;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void findByIsDeletedFalse_returnsOnlyActivePosts() {
        // given
        User user = new User("Username", "mail@mail.pl", "Password");
        userRepository.save(user);

        Post activePost = new Post("Active Post", "Content", user);

        Post deletedPost = new Post("Deleted Post", "Content", user);
        deletedPost.setDeleted(true);

        underTest.save(activePost);
        underTest.save(deletedPost);

        // when
        List<Post> result = underTest.findByIsDeletedFalse();

        // then
        assertThat(result).containsExactly(activePost);
        assertThat(result).doesNotContain(deletedPost);
    }

    @Test
    void markPostsDeletedByUserId_marksAllUserPostsAsDeleted() {
        // given
        User user = new User("Username", "mail@mail.pl", "Password");
        userRepository.save(user);

        Post post1 = new Post("Post 1", "Content 1", user);
        Post post2 = new Post("Post 2", "Content 2", user);
        underTest.save(post1);
        underTest.save(post2);

        // when
        underTest.markPostsDeletedByUserId(user.getId());

        entityManager.flush();
        entityManager.clear();

        // then
        List<Post> result = underTest.findAll();
        assertThat(result.get(0).isDeleted()).isEqualTo(true);
        assertThat(result.get(1).isDeleted()).isEqualTo(true);
    }
}
