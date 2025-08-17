package com.example.SpringBloggerAPI.user.role;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoleSeeder {

    @Bean
    public CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                for (RoleType roleType : RoleType.values()) {
                    roleRepository.save(new Role(roleType.getRoleName()));
                }
            }
        };
    }
}
