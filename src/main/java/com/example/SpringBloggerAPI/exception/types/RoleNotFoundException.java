package com.example.SpringBloggerAPI.exception.types;

import com.example.SpringBloggerAPI.user.role.RoleType;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(RoleType roleType) {
      super("Role not found: " + roleType);
    }
}
