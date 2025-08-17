package com.example.SpringBloggerAPI.auth;

import com.example.SpringBloggerAPI.auth.dto.LoginRequest;
import com.example.SpringBloggerAPI.auth.dto.AuthResponse;
import com.example.SpringBloggerAPI.auth.dto.SignupRequest;
import com.example.SpringBloggerAPI.exception.types.ConflictException;
import com.example.SpringBloggerAPI.exception.types.UserDeletedException;
import com.example.SpringBloggerAPI.service.JwtService;
import com.example.SpringBloggerAPI.user.role.Role;
import com.example.SpringBloggerAPI.user.role.RoleRepository;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.UserRepository;
import com.example.SpringBloggerAPI.user.role.RoleService;
import com.example.SpringBloggerAPI.user.role.RoleType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            RoleService roleService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public AuthResponse login(LoginRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        User user = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isDeleted()) {
            throw new UserDeletedException("This account has been deactivated");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(token);
    }

    public AuthResponse registerUser(SignupRequest request) {
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("This username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("This email is already taken");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setRoles(List.of(defaultRole));
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(newUser);

        String token = jwtService.generateToken(newUser.getUsername());
        return new AuthResponse(token);
    }

    public boolean isAdmin (User user) {
        return roleService.userHasRole(user, RoleType.ADMIN);
    }

    public void logoutCurrentUser() {
        SecurityContextHolder.clearContext();
    }


}
