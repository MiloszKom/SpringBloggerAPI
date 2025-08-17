package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.comment.CommentMapper;
import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.post.dto.PostDetailsDTO;
import com.example.SpringBloggerAPI.user.UserMapper;

import java.util.List;

public class PostMapper {

    public static PostDetailsDTO toPostDetailsDTO(Post post) {
        List<CommentDetailsDTO> comments = post.getComments() == null
                ? List.of()
                : post.getComments().stream()
                .map(CommentMapper::toCommentDetailsDTO)
                .toList();

        return new PostDetailsDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                UserMapper.toSummaryDTO(post.getUser()),
                comments
        );
    }
}
