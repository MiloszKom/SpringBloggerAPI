package com.example.SpringBloggerAPI.comment.dto;

import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;

public record CommentDetailsDTO(
        int id,
        String content,
        UserSummaryDTO user
) {}
