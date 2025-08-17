package com.example.SpringBloggerAPI.user;

import com.example.SpringBloggerAPI.auth.AuthService;
import com.example.SpringBloggerAPI.exception.types.PermissionDeniedException;
import com.example.SpringBloggerAPI.exception.types.UserGoneException;
import com.example.SpringBloggerAPI.exception.types.UserNotFoundException;
import com.example.SpringBloggerAPI.user.deletion.UserDeletionHandler;
import com.example.SpringBloggerAPI.user.dto.UserDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import com.example.SpringBloggerAPI.user.role.RoleService;
import com.example.SpringBloggerAPI.user.role.RoleType;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private RoleService roleService;

    @Mock
    private List<UserDeletionHandler> deletionHandlers;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepository, authService, roleService, deletionHandlers);
    }

    @Test
    void getUser_whenUserExistsAndNotDeleted_returnsUser() {
        User user = new User(1,"User", "user@mail.pl", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User result = underTest.getUser(1);

        AssertionsForClassTypes.assertThat(result).isNotNull();
        AssertionsForClassTypes.assertThat(result.getUsername()).isEqualTo("User");
    }

    @Test
    void getUser_whenUserDoesNotExist_throwsUserNotFoundException() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getUser(1))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void getUser_whenUserIsDeleted_throwsUserGoneException() {
        User user = new User(1,"User", "user@mail.pl", "password");
        user.setDeleted(true);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> underTest.getUser(1))
                .isInstanceOf(UserGoneException.class)
                .hasMessageContaining("1");
    }

    @Test
    void findAll_whenUsersExist_returnsListOfUserSummaries() {
        User activeUser1 = new User("Username1", "example@mail1.pl", "password");
        User activeUser2 = new User("Username2", "example@mail2.pl", "password");

        when(userRepository.findByIsDeletedFalse()).thenReturn(List.of(activeUser1, activeUser2));

        List<UserSummaryDTO> result = underTest.findAll();

        assertThat(result).hasSize(2);

        verify(userRepository).findByIsDeletedFalse();
    }

    @Test
    void findSingle_whenUserExists_returnsUserDetailsDTO() {
        User user = new User(1, "User", "user@mail.pl", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserDetailsDTO result = underTest.findSingle(1);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(user.getId());
        assertThat(result.username()).isEqualTo(user.getUsername());
        assertThat(result.email()).isEqualTo(user.getEmail());
    }

    @Test
    void grantAdminRole_userNotAdmin_assignsAdminRole() {
        User user = new User(1, "User", "user@mail.pl", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleService.userHasRole(user, RoleType.ADMIN)).thenReturn(false);

        underTest.grantAdminRole(1);

        verify(roleService).assignRoleToUser(user, RoleType.ADMIN);
    }

    @Test
    void grantAdminRole_userAlreadyAdmin_doesNotAssignAdminRole() {
        User user = new User(1, "User", "user@mail.pl", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(roleService.userHasRole(user, RoleType.ADMIN)).thenReturn(true);

        underTest.grantAdminRole(1);

        verify(roleService, never()).assignRoleToUser(user, RoleType.ADMIN);
    }

    @Test
    void deleteUser_nonAdminDeletesOwnAccount_succeedsAndLogsOut() {
        User user = new User(1, "User", "user@mail.pl", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getCurrentUser()).thenReturn(user);
        when(roleService.userHasRole(user, RoleType.ADMIN)).thenReturn(false);

        underTest.deleteUser(1);

        assertTrue(user.isDeleted());
        verify(userRepository).save(user);

        verify(authService).logoutCurrentUser();
    }

    @Test
    void deleteUser_nonAdminDeletesOtherAccount_throwsPermissionDenied() {
        User user1 = new User(1, "User1", "user@mail1.pl", "password");
        User user2 = new User(2, "User2", "user@mail2.pl", "password");

        when(userRepository.findById(1)).thenReturn(Optional.of(user1));
        when(authService.getCurrentUser()).thenReturn(user2);
        when(roleService.userHasRole(user2, RoleType.ADMIN)).thenReturn(false);

        assertThrows(PermissionDeniedException.class,
                () -> underTest.deleteUser(1));

        verify(userRepository, never()).save(user1);
        verify(authService, never()).logoutCurrentUser();
    }

    @Test
    void deleteUser_adminDeletesOtherAccount_succeedsWithoutLogout() {
        User admin = new User(1, "Admin", "admin@mail1.pl", "password");
        User user = new User(2, "User", "user@mail2.pl", "password");

        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(authService.getCurrentUser()).thenReturn(admin);
        when(roleService.userHasRole(admin, RoleType.ADMIN)).thenReturn(true);

        underTest.deleteUser(2);
        assertTrue(user.isDeleted());
        verify(userRepository).save(user);
        verify(authService, never()).logoutCurrentUser();
    }
}