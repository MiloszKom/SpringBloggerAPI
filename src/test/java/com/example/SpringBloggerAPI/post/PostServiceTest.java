package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.Comment;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.PostGoneException;
import com.example.SpringBloggerAPI.exception.types.PostNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostDetailsDTO;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthService authService;

    private PostService underTest;

    @BeforeEach
    void setUp() {
        underTest = new PostService(postRepository, authService);
    }

    @Test
    void getPost_whenPostExistsAndNotDeleted_returnsPost() {
        User user = new User("User", "user@mail.pl", "password");
        Post post = new Post("Title", "Content", user);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        Post result = underTest.getPost(1);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getContent()).isEqualTo("Content");
    }

    @Test
    void getPost_whenPostDoesNotExist_throwsPostNotFoundException() {
        when(postRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getPost(1))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void getPost_whenPostIsDeleted_throwsPostGoneException() {
        User user = new User("User", "user@mail.pl", "password");
        Post post = new Post("Title", "Content", user);
        post.setDeleted(true);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        assertThatThrownBy(() -> underTest.getPost(1))
                .isInstanceOf(PostGoneException.class)
                .hasMessageContaining("1");
    }

    @Test
    void savePost_withValidRequest_returnsSavedPostDto () {
        User mockUser = new User("User", "user@mail.pl", "password");
        when(authService.getCurrentUser()).thenReturn(mockUser);

        PostRequest request = new PostRequest("Title", "Content");

        Post savedPost = new Post(1,"Title", "Content", mockUser);
        when(postRepository.save(ArgumentMatchers.<Post>any())).thenReturn(savedPost);

        PostDetailsDTO result = underTest.savePost(request);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("Title");
        assertThat(result.content()).isEqualTo("Content");
        assertThat(result.user()).isNotNull();
        assertThat(result.user().username()).isEqualTo("User");

        verify(authService).getCurrentUser();
        verify(postRepository).save(ArgumentMatchers.<Post>any());
    }

    @Test
    void getAllPosts_returnsAllPostsDto() {
        User mockUser = new User("User", "user@mail.pl", "password");

        Post post1 = new Post(1, "Title1", "Content1", mockUser);
        Post post2 = new Post(2, "Title2", "Content2", mockUser);

        when(postRepository.findByIsDeletedFalse()).thenReturn(List.of(post1, post2));

        List<PostDetailsDTO> result = underTest.getAllPosts();

        assertThat(result).isNotNull();
        assertThat(result.get(0).title()).isEqualTo("Title1");
        assertThat(result.get(1).title()).isEqualTo("Title2");

        assertThat(result.get(0).user().username()).isEqualTo("User");
        assertThat(result.get(1).user().username()).isEqualTo("User");
    }

    @Test
    void getSinglePost_returnPostDTO() {
        User mockUser = new User(1,"User", "user@mail.pl", "password");
        Post post = new Post(1, "Title", "Content", mockUser);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        PostDetailsDTO result = underTest.getSinglePost(1);

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.title()).isEqualTo("Title");
        assertThat(result.user().username()).isEqualTo("User");
        assertThat(result.user().id()).isEqualTo(1);
    }

    @Test
    void updatePost_userUpdatedOwnPost_returnsUpdatedPost() {
        User mockUser = new User(1, "User", "user@mail.pl", "password");
        Post post = new Post(1, "Title", "Content", mockUser);
        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        PostRequest request = new PostRequest(newTitle, newContent);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));

        when(authService.getCurrentUser()).thenReturn(mockUser);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDetailsDTO result = underTest.updatePost(1, request);

        verify(postRepository).save(post);
        assertThat(result.title()).isEqualTo(newTitle);
        assertThat(result.content()).isEqualTo(newContent);
        assertThat(result.user().id()).isEqualTo(1);
        assertThat(result.user().username()).isEqualTo("User");
    }

    @Test
    void updatePost_userUpdatedOthersPost_throwsPermissionDenied() {
        User user1 = new User(1, "User1", "user@mail1.pl", "password");
        User user2 = new User(2, "User2", "user@mail2.pl", "password");

        Post post = new Post(1, "Title", "Content", user1);
        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        PostRequest request = new PostRequest(newTitle, newContent);

        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(authService.getCurrentUser()).thenReturn(user2);

        assertThrows(PermissionDeniedException.class,
                () -> underTest.updatePost(1, request));

        verify(postRepository, never()).save(post);
    }

    @Test
    void deletePost_userDeletesOwnPost_marksPostAsDeleted() {
        User user = new User(1, "User", "user@mail.pl", "password");
        Post post = new Post(1, "Title", "Content", user);
        Comment comment = new Comment(1, "Comment", user, post);

        post.setComments(new ArrayList<>(List.of(comment)));

        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(authService.getCurrentUser()).thenReturn(user);
        when(authService.isAdmin(user)).thenReturn(false);

        underTest.deletePost(1);

        assertThat(post.isDeleted()).isEqualTo(true);
        assertThat(post.getComments().get(0).isDeleted()).isEqualTo(true);
        verify(postRepository).save(post);
    }

    @Test
    void deletePost_userDeletesOthersPost_throwsPermissionDenied() {
        User user1 = new User(1, "User1", "user@mail1.pl", "password");
        User user2 = new User(2, "User2", "user@mail2.pl", "password");

        Post post = new Post(1, "Title", "Content", user1);


        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(authService.getCurrentUser()).thenReturn(user2);
        when(authService.isAdmin(user2)).thenReturn(false);


        assertThrows(PermissionDeniedException.class,
                () -> underTest.deletePost(1));

        assertThat(post.isDeleted()).isEqualTo(false);
        verify(postRepository, never()).save(post);
    }

    @Test
    void deletePost_adminDeletesOthersPost_marksPostAsDeleted() {
        User admin = new User(1, "Admin", "admin@mail.pl", "password");
        User user = new User(2, "User", "user@mail.pl", "password");

        Post post = new Post(1, "Title", "Content", user);


        when(postRepository.findById(1)).thenReturn(Optional.of(post));
        when(authService.getCurrentUser()).thenReturn(admin);
        when(authService.isAdmin(admin)).thenReturn(true);

        underTest.deletePost(1);

        assertThat(post.isDeleted()).isEqualTo(true);
        verify(postRepository).save(post);
    }
}
