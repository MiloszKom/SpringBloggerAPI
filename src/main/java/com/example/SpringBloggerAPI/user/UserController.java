package com.example.SpringBloggerAPI.user;
import com.example.SpringBloggerAPI.user.dto.UserDetailsDTO;
import com.example.SpringBloggerAPI.user.dto.UserSummaryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserSummaryDTO>> getUsers() {
        List<UserSummaryDTO> users = service.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable int id) {
        UserDetailsDTO user = service.findSingle(id);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/grant-admin")
    public ResponseEntity<String> grantAdminRole(@PathVariable int id) {
        service.grantAdminRole(id);
        return ResponseEntity.ok("Admin role granted to user with id " + id);
    }
}
