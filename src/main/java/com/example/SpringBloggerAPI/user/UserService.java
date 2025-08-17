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
import com.example.SpringBloggerAPI.user.deletion.UserDeletionHandler;
import com.example.SpringBloggerAPI.user.role.*;
import com.example.SpringBloggerAPI.user.dto.UserDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final RoleService roleService;
    private final List<UserDeletionHandler> deletionHandlers;

    public UserService(
            UserRepository userRepository,
            AuthService authService,
            RoleService roleService,
            List<UserDeletionHandler> deletionHandlers
    ) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.roleService = roleService;
        this.deletionHandlers = deletionHandlers;
    }

    public User getUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(user.isDeleted()) {
            throw new UserGoneException("User with id " + id + " has been deleted");
        }

        return user;
    }

    public List<UserSummaryDTO> findAll() {
        List<User> users = userRepository.findByIsDeletedFalse();

        return users.stream()
                .map(UserMapper::toSummaryDTO)
                .toList();
    }

    public UserDetailsDTO findSingle(int id) {
        User user = getUser(id);
        return UserMapper.toDetailsDTO(user);
    }

    public void grantAdminRole(int id) {
        User user = getUser(id);

        if (!roleService.userHasRole(user, RoleType.ADMIN)) {
            roleService.assignRoleToUser(user, RoleType.ADMIN);
        }
    }

    public void deleteUser(int id) {
        User user = getUser(id);
        User currentUser = authService.getCurrentUser();

        boolean isAdmin = roleService.userHasRole(currentUser, RoleType.ADMIN);
        boolean isAccountOwner = user.getId() == currentUser.getId();

        if (!isAdmin && !isAccountOwner) {
            throw new PermissionDeniedException("You are not the owner of this account");
        }

        user.setDeleted(true);
        userRepository.save(user);

        deletionHandlers.forEach(handler -> handler.handleUserDeletion(id));

        if (isAccountOwner) {
            authService.logoutCurrentUser();
        }
    }
}
