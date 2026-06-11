package com.example.courseprojectapi.service;

import com.example.courseprojectapi.model.dto.response.CourseResponse;
import com.example.courseprojectapi.model.entity.Course;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.CourseRepository;
import com.example.courseprojectapi.repository.UserRepository;
import com.example.courseprojectapi.security.principle.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void enrollCourse(Long courseId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học"));

        if (student.getCourses().contains(course)) {
            throw new RuntimeException("Đã đăng ký khóa học này rồi");
        }

        student.getCourses().add(course);
        userRepository.save(student);
    }

    public List<CourseResponse> getEnrolledCourses() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên"));

        return student.getCourses().stream()
                .map(course -> CourseResponse.builder()
                        .id(course.getId())
                        .courseCode(course.getCourseCode())
                        .courseName(course.getCourseName())
                        .credit(course.getCredit())
                        .build())
                .collect(Collectors.toList());
    }
}
