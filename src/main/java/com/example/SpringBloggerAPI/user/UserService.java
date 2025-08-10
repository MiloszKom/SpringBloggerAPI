package com.example.SpringBloggerAPI.user;

import com.example.SpringBloggerAPI.exception.types.UserNotFoundException;
import com.example.SpringBloggerAPI.post.dto.PostSummaryDTO;
import com.example.SpringBloggerAPI.role.Role;
import com.example.SpringBloggerAPI.role.RoleRepository;
import com.example.SpringBloggerAPI.user.dto.UserDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUser(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Post not found with id: " + id));
    }

    public List<UserSummaryDTO> findAll() {
        List<User> users = userRepository.findAll();

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
