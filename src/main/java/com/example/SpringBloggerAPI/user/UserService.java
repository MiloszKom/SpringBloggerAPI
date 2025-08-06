package com.example.SpringBloggerAPI.user;

import com.example.SpringBloggerAPI.comment.Comment;
import com.example.SpringBloggerAPI.comment.dto.CommentResponse;
import com.example.SpringBloggerAPI.user.dto.UserSummary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<UserSummary> findAll() {
        List<User> users = repository.findAll();

        return users.stream()
                .map(this::mapUserToDto)
                .toList();
    }

    public UserSummary mapUserToDto(User user) {
        return new UserSummary(user.getId(), user.getUsername());
    }
}
