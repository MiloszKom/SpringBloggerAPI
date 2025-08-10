package com.example.SpringBloggerAPI.post.dto;

import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;

import java.util.List;

public record PostDetailsDTO(
        int id,
        String title,
        String content,
        UserSummaryDTO user,
        List<CommentDetailsDTO> comments
) {}
