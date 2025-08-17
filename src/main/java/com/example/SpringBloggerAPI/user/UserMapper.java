package com.example.SpringBloggerAPI.user;

import com.example.SpringBloggerAPI.post.dto.PostSummaryDTO;
import com.example.SpringBloggerAPI.user.dto.UserDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;

import java.util.List;

public class UserMapper {

    public static UserSummaryDTO toSummaryDTO(User user) {
        return new UserSummaryDTO(user.getId(), user.getUsername());
    }

    public static UserDetailsDTO toDetailsDTO(User user) {
        List<PostSummaryDTO> postSummaries = user.getPosts() == null
                ? List.of()
                : user.getPosts().stream()
                .map(post -> new PostSummaryDTO(post.getId(), post.getTitle()))
                .toList();

        return new UserDetailsDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                postSummaries
        );
    }
}
