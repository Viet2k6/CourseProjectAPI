package com.example.courseprojectapi.controller;

import com.example.courseprojectapi.model.dto.response.BaseResponse;
import com.example.courseprojectapi.model.dto.response.CourseResponse;
import com.example.courseprojectapi.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/courses/enroll/{courseId}")
    public ResponseEntity<?> enrollCourse(@PathVariable Long courseId) {
        studentService.enrollCourse(courseId);
        return new ResponseEntity<>(BaseResponse.success("Đăng ký khóa học thành công", null), HttpStatus.CREATED);
    }

    @GetMapping("/courses")
    public ResponseEntity<?> getEnrolledCourses() {
        return ResponseEntity.ok(BaseResponse.success("Lấy danh sách khóa học đã đăng ký thành công", studentService.getEnrolledCourses()));
    }
}
