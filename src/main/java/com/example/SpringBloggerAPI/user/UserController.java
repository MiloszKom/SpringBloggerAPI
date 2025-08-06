package com.example.SpringBloggerAPI.user;
import com.example.SpringBloggerAPI.user.dto.UserSummary;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<UserSummary>> getUsers() {
        List<UserSummary> users = service.findAll();
        return ResponseEntity.ok(users);
    }

}
