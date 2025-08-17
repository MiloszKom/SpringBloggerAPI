package com.example.SpringBloggerAPI.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindByUsername() {
        // given
        String username = "TestUser";
        User user = new User();

        user.setUsername(username);
        user.setEmail("test@mail.pl");
        user.setPassword("password");

        underTest.save(user);
        // when

        Optional<User> expected = underTest.findByUsername(username);

        // then
        assertThat(expected).isPresent();
    }

    @Test
    void itShouldNotFindUserIfUsernameDoesNotExist() {
        // given
        String username = "NonExistentUser";

        // when
        Optional<User> result = underTest.findByUsername(username);

        // then
        assertThat(result).isNotPresent();
    }
}