package com.example.courseprojectapi.service;

import com.example.courseprojectapi.model.dto.request.LoginRequest;
import com.example.courseprojectapi.model.dto.request.RegisterRequest;
import com.example.courseprojectapi.model.dto.response.AuthResponse;
import com.example.courseprojectapi.model.entity.Role;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.RoleRepository;
import com.example.courseprojectapi.repository.UserRepository;
import com.example.courseprojectapi.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Lỗi: Người dùng không tồn tại!"));

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Lỗi: Tên đăng nhập đã tồn tại!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Lỗi: Email đã được sử dụng!");
        }

        Role studentRole = roleRepository.findByRoleName("STUDENT")
                .orElseGet(() -> {
                    Role role = Role.builder().roleName("STUDENT").build();
                    return roleRepository.save(role);
                });

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .role(studentRole)
                .build();

        userRepository.save(user);
    }
}
