package com.example.SpringBloggerAPI.user.dto;

import com.example.SpringBloggerAPI.post.dto.PostSummaryDTO;

import java.util.List;

public record UserDetailsDTO(
        int id,
        String username,
        String email,
        List<PostSummaryDTO> posts
) {}
