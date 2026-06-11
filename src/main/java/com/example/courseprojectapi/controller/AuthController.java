package com.example.courseprojectapi.controller;

import com.example.courseprojectapi.model.dto.request.LoginRequest;
import com.example.courseprojectapi.model.dto.request.RegisterRequest;
import com.example.courseprojectapi.model.dto.response.AuthResponse;
import com.example.courseprojectapi.model.dto.response.BaseResponse;
import com.example.courseprojectapi.service.AuthService;
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
}
