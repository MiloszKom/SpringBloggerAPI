package com.example.SpringBloggerAPI.user.role;

import com.example.SpringBloggerAPI.exception.types.RoleNotFoundException;
import com.example.SpringBloggerAPI.user.User;
import com.example.SpringBloggerAPI.user.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public RoleService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private Role getRoleOrThrow(RoleType roleType) {
        return roleRepository.findByName(roleType.getRoleName())
                .orElseThrow(() -> new RoleNotFoundException(roleType));
    }

    public void assignRoleToUser(User user, RoleType roleType) {
        Role role = getRoleOrThrow(roleType);

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    public void removeRoleFromUser(User user, RoleType roleType) {
        Role role = getRoleOrThrow(roleType);

        if (user.getRoles().contains(role)) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }
    }

    public boolean userHasRole(User user, RoleType roleType) {
        return user.getRoles().stream().anyMatch(r -> r.getName().equals(roleType.getRoleName()));
    }
}
