package com.example.SpringBloggerAPI.user;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.comment.CommentRepository;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.PostGoneException;
import com.example.SpringBloggerAPI.exception.types.UserGoneException;
import com.example.SpringBloggerAPI.exception.types.UserNotFoundException;
import com.example.SpringBloggerAPI.post.Post;
import com.example.SpringBloggerAPI.post.PostRepository;
import com.example.SpringBloggerAPI.post.dto.PostSummaryDTO;
import com.example.SpringBloggerAPI.user.role.Role;
import com.example.SpringBloggerAPI.user.role.RoleRepository;
import com.example.SpringBloggerAPI.user.dto.UserDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthService authService,
            PostRepository postRepository,
            CommentRepository commentRepository
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authService = authService;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Post not found with id: " + id));

        if(user.isDeleted()) {
            throw new UserGoneException("User with id " + id + " has been deleted");
        }

        return user;
    }

    public List<UserSummaryDTO> findAll() {
        List<User> users = userRepository.findByIsDeletedFalse();

        return users.stream()
                .map(this::mapUserToDto)
                .toList();
    }

    public UserDetailsDTO findSingle(int id) {
        User user = getUser(id);
        return mapUserToDetailsDto(user);
    }

    public void grantAdminRole(int id) {
        User user = getUser(id);

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

        if (!user.getRoles().contains(adminRole)) {
            user.getRoles().add(adminRole);
            userRepository.save(user);
        }
    }

    public void deleteUser(int id) {
        User user = getUser(id);
        User currentUser = authService.getCurrentUser();

        boolean isAdmin = authService.isAdmin(currentUser);
        boolean isAccountOwner = user.getId() == currentUser.getId();

        if (!isAdmin && !isAccountOwner) {
            throw new PermissionDeniedException("You are not the owner of this account");
        }

        user.setDeleted(true);
        userRepository.save(user);

        commentRepository.markCommentsDeletedByUserId(id);

        commentRepository.markCommentsDeletedByPostUserId(id);

        postRepository.markPostsDeletedByUserId(id);

        if (isAccountOwner) {
            authService.logoutCurrentUser();
        }
    }

    public UserSummaryDTO mapUserToDto(User user) {
        return new UserSummaryDTO(user.getId(), user.getUsername());
    }

    private UserDetailsDTO mapUserToDetailsDto(User user) {
        List<PostSummaryDTO> postSummaries = user.getPosts().stream()
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
