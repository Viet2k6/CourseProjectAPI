package com.example.courseprojectapi.service;

import com.example.courseprojectapi.exception.CustomException;
import com.example.courseprojectapi.exception.NotFoundException;
import com.example.courseprojectapi.model.dto.request.SubmissionRequest;
import com.example.courseprojectapi.model.dto.response.CourseResponse;
import com.example.courseprojectapi.model.entity.Course;
import com.example.courseprojectapi.model.entity.Submission;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.CourseRepository;
import com.example.courseprojectapi.repository.SubmissionRepository;
import com.example.courseprojectapi.repository.UserRepository;
import com.example.courseprojectapi.security.principle.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void submitAssignment(SubmissionRequest request, MultipartFile file) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = userRepository.findById(userPrincipal.getId()).get();
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Khóa học không tồn tại"));

        if (!student.getCourses().contains(course)) {
            throw new CustomException("Bạn chưa đăng ký khóa học này", HttpStatus.FORBIDDEN);
        }

        // TỐI ƯU: Kiểm tra nếu đã nộp rồi thì cập nhật, chưa nộp thì mới tạo mới
        Submission submission = submissionRepository.findByStudentAndCourse(student, course)
                .orElse(new Submission());

        String reportUrl = submission.getReportUrl();
        if (file != null && !file.isEmpty()) {
            reportUrl = cloudinaryService.uploadImage(file);
        }

        submission.setGithubUrl(request.getGithubUrl());
        submission.setReportUrl(reportUrl);
        submission.setStatus("SUBMITTED");
        submission.setStudent(student);
        submission.setCourse(course);

        submissionRepository.save(submission);
    }

    @Transactional
    public void enrollCourse(Long courseId) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học"));

        if (student.getCourses().contains(course)) {
            throw new CustomException("Đã đăng ký khóa học này rồi", HttpStatus.BAD_REQUEST);
        }

        student.getCourses().add(course);
        userRepository.save(student);
    }

    public List<CourseResponse> getEnrolledCourses() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy sinh viên"));

        return student.getCourses().stream()
                .map(CourseResponse::from)
                .collect(Collectors.toList());
    }
}
