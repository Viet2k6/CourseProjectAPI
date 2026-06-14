package com.example.courseprojectapi.service;

import com.example.courseprojectapi.exception.CustomException;
import com.example.courseprojectapi.exception.NotFoundException;
import com.example.courseprojectapi.model.dto.request.ChangePasswordRequest;
import com.example.courseprojectapi.model.dto.request.LoginRequest;
import com.example.courseprojectapi.model.dto.request.RegisterRequest;
import com.example.courseprojectapi.model.dto.response.AuthResponse;
import com.example.courseprojectapi.model.entity.Role;
import com.example.courseprojectapi.model.entity.TokenBlacklist;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.RoleRepository;
import com.example.courseprojectapi.repository.TokenBlacklistRepository;
import com.example.courseprojectapi.repository.UserRepository;
import com.example.courseprojectapi.security.jwt.JwtProvider;
import com.example.courseprojectapi.security.principle.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("Lỗi: Người dùng không tồn tại!", HttpStatus.UNAUTHORIZED));

        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new CustomException("Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email không tồn tại trong hệ thống"));
        
        user.setPasswordHash(passwordEncoder.encode("12345678"));
        userRepository.save(user);
    }

    @Transactional
    public void logout(HttpServletRequest request, String refreshToken) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            String accessToken = headerAuth.substring(7);
            addToBlacklist(accessToken);
        }

        if (refreshToken != null && !refreshToken.isEmpty()) {
            addToBlacklist(refreshToken);
        }
    }

    private void addToBlacklist(String token) {
        if (!tokenBlacklistRepository.existsByTokenString(token)) {
            User user = null;
            try {
                String username = jwtProvider.getUsernameFromToken(token);
                user = userRepository.findByUsername(username).orElse(null);
            } catch (Exception e) {
            }
            
            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .tokenString(token)
                    .revokedAt(LocalDateTime.now())
                    .user(user)
                    .build();
            tokenBlacklistRepository.save(blacklist);
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
    if (tokenBlacklistRepository.existsByTokenString(refreshToken)) {
        throw new CustomException("Refresh Token đã bị vô hiệu hóa", HttpStatus.UNAUTHORIZED);
    }

    if (refreshToken != null && jwtProvider.validateRefreshToken(refreshToken)) {
        String username = jwtProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));

        if (!user.getIsActive()) {
            throw new CustomException("Tài khoản của bạn đã bị khóa", HttpStatus.FORBIDDEN);
        }

        String newAccessToken = jwtProvider.generateAccessToken(user);
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }
        throw new CustomException("Refresh Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException("Lỗi: Tên đăng nhập đã tồn tại!", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Lỗi: Email đã được sử dụng!", HttpStatus.CONFLICT);
        }

        Role studentRole = roleRepository.findByRoleName("STUDENT")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("STUDENT").build()));

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
