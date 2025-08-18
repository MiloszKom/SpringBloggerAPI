package com.example.SpringBloggerAPI.user;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.post.Post;
import com.example.SpringBloggerAPI.user.role.RoleService;
import com.example.SpringBloggerAPI.user.role.RoleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private AuthService authService;

    @MockBean
    private RoleService roleService;

    private User user1;
    private User user2;
    private User admin;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        user1 = new User("user1", "user1@example.com", "password");
        userRepository.save(user1);

        user2 = new User("user2", "user2@example.com", "password");
        userRepository.save(user2);

        admin = new User("Admin", "admin@mail.com", "password");
        userRepository.save(admin);
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void getAllUsers_userIsLoggedIn_returnsListOfUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());

    }

    @Test
    void getAllUsers_userIsNotLoggedIn_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void getUser_existingUser_returnsOk() throws Exception {
        mockMvc.perform(get("/api/users/" + user1.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void getUser_nonExistingUser_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void deleteUser_userDeletesOwnAccount_returnsNoContentAndLogsOut() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);
        when(roleService.userHasRole(user1, RoleType.ADMIN)).thenReturn(false);

        mockMvc.perform(delete("/api/users/" + user1.getId()))
                .andExpect(status().isNoContent());

        verify(authService).logoutCurrentUser();
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void deleteUser_userTriesToDeleteOtherUser_returnsForbidden() throws Exception {
        when(authService.getCurrentUser()).thenReturn(user1);
        when(roleService.userHasRole(user1, RoleType.ADMIN)).thenReturn(false);

        mockMvc.perform(delete("/api/users/" + user2.getId()))
                .andExpect(status().isForbidden());

        verify(authService, never()).logoutCurrentUser();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_adminDeletesUser_returnsNoContent() throws Exception {
        when(authService.getCurrentUser()).thenReturn(admin);
        when(roleService.userHasRole(admin, RoleType.ADMIN)).thenReturn(true);

        mockMvc.perform(delete("/api/users/" + user1.getId()))
                .andExpect(status().isNoContent());

        verify(authService, never()).logoutCurrentUser();
    }

}
