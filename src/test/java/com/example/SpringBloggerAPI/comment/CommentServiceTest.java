package com.example.SpringBloggerAPI.comment;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.dto.CommentDetailsDTO;
import com.example.SpringBloggerAPI.comment.dto.CommentRequest;
import com.example.SpringBloggerAPI.exception.types.CommentGoneException;
import com.example.SpringBloggerAPI.exception.types.CommentNotFoundException;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.post.Post;
import com.example.SpringBloggerAPI.post.PostService;
import com.example.SpringBloggerAPI.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private AuthService authService;

    private CommentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CommentService(commentRepository, postService, authService);
    }

    @Test
    void createComment_returnsCreatedCommentDTO() {
        User mockUser = new User(1, "Username", "mail@mail.pl", "Password");
        Post mockPost = new Post(1, "Title", "Content", mockUser);

        when(authService.getCurrentUser()).thenReturn(mockUser);
        when(postService.getPost(1)).thenReturn(mockPost);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentRequest commentRequest = new CommentRequest("My new comment");

        CommentDetailsDTO result = underTest.createComment(1, commentRequest);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("My new comment");
        assertThat(result.user().username()).isEqualTo("Username");

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void getCommentsByPost_returnsListOfCommentsDTOs() {
        User postAuthor = new User(1, "Username1", "mail@mail1.pl", "Password");
        Post post = new Post(1, "Title", "Content", postAuthor);

        User commentAuthor1 = new User(2, "Username2", "mail@mail2.pl", "Password");
        User commentAuthor2 = new User(3, "Username3", "mail@mail3.pl", "Password");

        Comment comment1 = new Comment(1, "Comment nr 1", commentAuthor1, post);
        Comment comment2 = new Comment(2, "Comment nr 2", commentAuthor2, post);

        post.setComments(List.of(comment1, comment2));

        when(postService.getPost(1)).thenReturn(post);

        List<CommentDetailsDTO> result = underTest.getCommentsByPost(1);

        assertThat(result.get(0).content()).isEqualTo("Comment nr 1");
        assertThat(result.get(0).user().username()).isEqualTo("Username2");
        assertThat(result.get(1).content()).isEqualTo("Comment nr 2");
        assertThat(result.get(1).user().username()).isEqualTo("Username3");
    }

    @Test
    void getSingleComment_returnsCommentDetailsDTO_whenValid() {
        User user = new User(1, "Username", "mail@mail.pl", "Password");
        Post post = new Post(1, "Title", "Content", user);

        Comment comment = new Comment(10, "My comment", user, post);
        comment.setDeleted(false);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));

        CommentDetailsDTO result = underTest.getSingleComment(1, 10);

        assertThat(result).isNotNull();
        assertThat(result.content()).isEqualTo("My comment");
        assertThat(result.user().username()).isEqualTo("Username");

        verify(postService).getPost(1);
        verify(commentRepository).findById(10);
    }

    @Test
    void getSingleComment_throwsIllegalArgumentException_whenCommentNotInPost() {
        User user = new User(1, "Username", "mail@mail.pl", "Password");
        Post correctPost = new Post(1, "Title", "Content", user);
        Post wrongPost = new Post(2, "Other", "Other", user);

        Comment comment = new Comment(10, "Other comment", user, wrongPost);

        when(postService.getPost(1)).thenReturn(correctPost);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class,
                () -> underTest.getSingleComment(1, 10));
    }

    @Test
    void getSingleComment_throwsCommentNotFoundException_whenCommentMissing() {
        User user = new User(1, "Username", "mail@mail.pl", "Password");
        Post post = new Post(1, "Title", "Content", user);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class,
                () -> underTest.getSingleComment(1, 10));
    }

    @Test
    void getSingleComment_throwsCommentGoneException_whenCommentDeleted() {
        User user = new User(1, "Username", "mail@mail.pl", "Password");
        Post post = new Post(1, "Title", "Content", user);

        Comment comment = new Comment(10, "Deleted comment", user, post);
        comment.setDeleted(true);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));

        assertThrows(CommentGoneException.class,
                () -> underTest.getSingleComment(1, 10));
    }

    @Test
    void updateComment_userUpdatesOwnComment_returnsUpdatedPostDTO() {
        User user = new User(1, "Username", "mail@mail.pl", "Password");
        Post post = new Post(1, "Title", "Content", user);
        Comment comment = new Comment(10, "Comment", user, post);

        String newComment = "Updated Comment";
        CommentRequest request = new CommentRequest(newComment);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommentDetailsDTO result = underTest.updateComment(post.getId(), comment.getId(), request);

        assertThat(result.content()).isEqualTo(newComment);
        assertThat(result.user().username()).isEqualTo("Username");

        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_userUpdatesOthersComment_throwsPermissionDenied() {
        User user1 = new User(1, "User1", "mail1@mail.pl", "password");
        User user2 = new User(2, "User2", "mail2@mail.pl", "password");

        Post post = new Post(1, "Title", "Content", user1);
        Comment comment = new Comment(10, "Comment", user1, post);

        CommentRequest request = new CommentRequest("Updated Comment");

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(user2);

        assertThrows(PermissionDeniedException.class,
                () -> underTest.updateComment(post.getId(), comment.getId(), request));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_commentNotInPost_throwsIllegalArgument() {
        User user = new User(1, "User", "mail@mail.pl", "password");

        Post post1 = new Post(1, "Title1", "Content1", user);
        Post post2 = new Post(2, "Title2", "Content2", user);
        Comment comment = new Comment(10, "Comment", user, post2);

        CommentRequest request = new CommentRequest("Updated Comment");

        when(postService.getPost(1)).thenReturn(post1);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(user);

        assertThrows(IllegalArgumentException.class,
                () -> underTest.updateComment(post1.getId(), comment.getId(), request));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_userDeletesOwnComment_marksCommentAsDeleted() {
        User user = new User(1, "Username", "mail@mail.pl", "Password");
        Post post = new Post(1, "Title", "Content", user);
        Comment comment = new Comment(10, "Comment", user, post);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(user);
        when(authService.isAdmin(user)).thenReturn(false);

        underTest.deleteComment(post.getId(), comment.getId());

        assertThat(comment.isDeleted()).isEqualTo(true);

        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_userDeletesOthersComment_throwsPermissionDenied() {
        User user1 = new User(1, "User1", "user1@mail.pl", "password");
        User user2 = new User(2, "User2", "user2@mail.pl", "password");

        Post post = new Post(1, "Title", "Content", user1);
        Comment comment = new Comment(10, "Comment", user1, post);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(user2);
        when(authService.isAdmin(user2)).thenReturn(false);

        assertThrows(PermissionDeniedException.class,
                () -> underTest.deleteComment(post.getId(), comment.getId()));

        assertThat(comment.isDeleted()).isFalse();
        verify(commentRepository, never()).save(comment);
    }

    @Test
    void deleteComment_adminDeletesOthersComment_marksCommentAsDeleted() {
        User admin = new User(1, "Admin", "admin@mail.pl", "password");
        User user = new User(2, "User", "user@mail.pl", "password");

        Post post = new Post(1, "Title", "Content", user);
        Comment comment = new Comment(10, "Comment", user, post);

        when(postService.getPost(1)).thenReturn(post);
        when(commentRepository.findById(10)).thenReturn(Optional.of(comment));
        when(authService.getCurrentUser()).thenReturn(admin);
        when(authService.isAdmin(admin)).thenReturn(true);

        underTest.deleteComment(post.getId(), comment.getId());

        assertThat(comment.isDeleted()).isTrue();
        verify(commentRepository).save(comment);
    }
}
