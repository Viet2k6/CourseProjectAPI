package com.example.courseprojectapi.config;

import com.example.courseprojectapi.model.entity.Role;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.RoleRepository;
import com.example.courseprojectapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initializeRoles();
        initializeAdmin();
    }

    private void initializeRoles() {
        if (roleRepository.findByRoleName("ADMIN").isEmpty()) {
            roleRepository.save(Role.builder().roleName("ADMIN").build());
        }
        if (roleRepository.findByRoleName("LECTURER").isEmpty()) {
            roleRepository.save(Role.builder().roleName("LECTURER").build());
        }
        if (roleRepository.findByRoleName("STUDENT").isEmpty()) {
            roleRepository.save(Role.builder().roleName("STUDENT").build());
        }
    }

    private void initializeAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElseThrow();
            User admin = User.builder()
                    .username("admin")
                    .email("admin@gmail.com")
                    .passwordHash(passwordEncoder.encode("12345678"))
                    .isActive(true)
                    .role(adminRole)
                    .build();
            userRepository.save(admin);
        }
    }
}
