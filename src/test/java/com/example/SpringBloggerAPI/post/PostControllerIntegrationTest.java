package com.example.SpringBloggerAPI.post;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.post.dto.PostRequest;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class PostControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    private User user1;
    private User user2;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setup() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        user1 = new User("user1", "user1@example.com", "password");
        userRepository.save(user1);

        user2 = new User("user2", "user2@example.com", "password");
        userRepository.save(user2);

        post1 = new Post("First Post", "Content of the first post", user1);
        postRepository.save(post1);

        post2 = new Post("Second Post", "Content of the second post", user2);
        postRepository.save(post2);
    }

    @Test
    void getAllPosts_returnsOkAndJson() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void savePost_validRequest_returnsCreatedPost() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);

        String postTitle = "Post Title";
        String postContent = "Post Content";

        PostRequest request = new PostRequest(postTitle, postContent);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(postTitle))
                .andExpect(jsonPath("$.content").value(postContent));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void savePost_missingContent_returnsBadRequest() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);
        String postTitle = "Post Title";

        String postContent = "";

        PostRequest request = new PostRequest(postTitle, postContent);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());

    }

    @Test
    void getPost_existingPost_returnsOkAndJson() throws Exception {
        mockMvc.perform(get("/api/posts/" + post1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(post1.getTitle()))
                .andExpect(jsonPath("$.content").value(post1.getContent()));
    }

    @Test
    void getPost_nonExistentPost_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void updatePost_existingPostByOwner_returnsOkAndUpdatedJson() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);

        String postTitle = "Updated Title";
        String postContent = "Updated Content";

        PostRequest request = new PostRequest(postTitle, postContent);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/posts/" + post1.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(postTitle))
                .andExpect(jsonPath("$.content").value(postContent));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void updatePost_otherUsersPost_returnsForbidden() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);

        String postTitle = "Updated Title";
        String postContent = "Updated Content";

        PostRequest request = new PostRequest(postTitle, postContent);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/posts/" + + post2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void deletePost_existingPostByOwner_returnsOk() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);

        mockMvc.perform(delete("/api/posts/" + + post1.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void deletePost_otherUsersPost_returnsForbidden() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);

        mockMvc.perform(delete("/api/posts/" + post2.getId()))
                .andExpect(status().isForbidden());
    }
}
