package com.example.courseprojectapi.controller;

import com.example.courseprojectapi.model.dto.request.LoginRequest;
import com.example.courseprojectapi.model.dto.request.RegisterRequest;
import com.example.courseprojectapi.model.dto.request.ChangePasswordRequest;
import com.example.courseprojectapi.model.dto.response.BaseResponse;
import com.example.courseprojectapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(BaseResponse.success("Đăng nhập thành công!", authService.login(loginRequest)));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return new ResponseEntity<>(BaseResponse.success("Đăng ký người dùng thành công!", null), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(BaseResponse.success("Đăng xuất thành công", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) {
        return ResponseEntity.ok(BaseResponse.success("Làm mới token thành công", authService.refreshToken(refreshToken)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(BaseResponse.success("Đổi mật khẩu thành công", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(BaseResponse.success("Mật khẩu đã được đặt lại thành công. Vui lòng kiểm tra thông báo mới nhất.", null));
    }
}
