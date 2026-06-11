package com.example.courseprojectapi.controller;

import com.example.courseprojectapi.model.dto.request.AdminUserRequest;
import com.example.courseprojectapi.model.dto.request.CourseRequest;
import com.example.courseprojectapi.model.dto.response.BaseResponse;
import com.example.courseprojectapi.model.dto.response.CourseResponse;
import com.example.courseprojectapi.model.dto.response.UserResponse;
import com.example.courseprojectapi.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String search, 
            Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Lấy danh sách người dùng thành công", adminService.getAllUsers(search, pageable)));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody AdminUserRequest request) {
        return new ResponseEntity<>(BaseResponse.success("Tạo người dùng thành công", adminService.createUser(request)), HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody AdminUserRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Cập nhật người dùng thành công", adminService.updateUser(id, request)));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses(
            @RequestParam(required = false) String search, 
            Pageable pageable) {
        return ResponseEntity.ok(BaseResponse.success("Lấy danh sách khóa học thành công", adminService.getAllCourses(search, pageable)));
    }

    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@Valid @RequestBody CourseRequest request) {
        return new ResponseEntity<>(BaseResponse.success("Tạo khóa học thành công", adminService.createCourse(request)), HttpStatus.CREATED);
    }

    @PutMapping("/courses/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        return ResponseEntity.ok(BaseResponse.success("Cập nhật khóa học thành công", adminService.updateCourse(id, request)));
    }

    @DeleteMapping("/courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        adminService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
