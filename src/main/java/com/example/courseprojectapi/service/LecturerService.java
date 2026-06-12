package com.example.courseprojectapi.service;

import com.example.courseprojectapi.exception.CustomException;
import com.example.courseprojectapi.exception.NotFoundException;
import com.example.courseprojectapi.model.dto.request.MaterialRequest;
import com.example.courseprojectapi.model.dto.response.MaterialResponse;
import com.example.courseprojectapi.model.dto.response.SubmissionResponse;
import com.example.courseprojectapi.model.entity.Course;
import com.example.courseprojectapi.model.entity.Material;
import com.example.courseprojectapi.model.entity.Submission;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.CourseRepository;
import com.example.courseprojectapi.repository.MaterialRepository;
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
public class LecturerService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final MaterialRepository materialRepository;
    private final SubmissionRepository submissionRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public MaterialResponse uploadMaterial(MaterialRequest request, MultipartFile file) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException("Khóa học không tồn tại"));

        if (!course.getLecturer().getId().equals(userPrincipal.getId())) {
            throw new CustomException("Bạn không có quyền đăng tài liệu cho khóa học này", HttpStatus.FORBIDDEN);
        }

        String fileUrl = cloudinaryService.uploadImage(file);
        
        Material material = Material.builder()
                .title(request.getTitle())
                .fileUrl(fileUrl)
                .course(course)
                .build();

        return MaterialResponse.from(materialRepository.save(material));
    }

    @Transactional
    public void gradeSubmission(Long submissionId, Double score, String feedback) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Bài nộp không tồn tại"));

        if (!submission.getCourse().getLecturer().getId().equals(userPrincipal.getId())) {
            throw new CustomException("Bạn không có quyền chấm bài nộp này", HttpStatus.FORBIDDEN);
        }

        submission.setScore(score);
        submission.setFeedback(feedback);
        submission.setStatus("GRADED");
        submission.setLecturer(userRepository.findById(userPrincipal.getId()).get());

        submissionRepository.save(submission);
    }

    public List<SubmissionResponse> getSubmissionsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Khóa học không tồn tại"));
        
        return course.getSubmissions().stream()
                .map(SubmissionResponse::from)
                .collect(Collectors.toList());
    }
}
