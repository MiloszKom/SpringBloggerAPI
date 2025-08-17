package com.example.SpringBloggerAPI.comment;

import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.user.UserMapper;

public class CommentMapper {

    public static CommentDetailsDTO toCommentDetailsDTO(Comment comment) {
        return new CommentDetailsDTO(
                comment.getId(),
                comment.getContent(),
                UserMapper.toSummaryDTO(comment.getUser())
        );
    }
}