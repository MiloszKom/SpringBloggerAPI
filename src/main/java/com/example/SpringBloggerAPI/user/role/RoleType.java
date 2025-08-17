package com.example.SpringBloggerAPI.user.role;

public enum RoleType {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String roleName;

    RoleType(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
