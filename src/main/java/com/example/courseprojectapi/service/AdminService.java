package com.example.courseprojectapi.service;

import com.example.courseprojectapi.exception.CustomException;
import com.example.courseprojectapi.exception.NotFoundException;
import com.example.courseprojectapi.model.dto.request.AdminUserRequest;
import com.example.courseprojectapi.model.dto.request.CourseRequest;
import com.example.courseprojectapi.model.dto.response.CourseResponse;
import com.example.courseprojectapi.model.dto.response.UserResponse;
import com.example.courseprojectapi.model.entity.Course;
import com.example.courseprojectapi.model.entity.Role;
import com.example.courseprojectapi.model.entity.User;
import com.example.courseprojectapi.repository.CourseRepository;
import com.example.courseprojectapi.repository.RoleRepository;
import com.example.courseprojectapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public Page<UserResponse> getAllUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable).map(UserResponse::from);
    }

    @Transactional
    public UserResponse createUser(AdminUserRequest request) {
        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền: " + request.getRoleName()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .role(role)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, AdminUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        user.setIsActive(request.getIsActive());

        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy quyền: " + request.getRoleName()));
        user.setRole(role);

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy người dùng để xóa");
        }
        userRepository.deleteById(id);
    }


    public Page<CourseResponse> getAllCourses(String search, Pageable pageable) {
        return courseRepository.searchCourses(search, pageable).map(CourseResponse::from);
    }

    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new CustomException("Mã khóa học đã tồn tại", HttpStatus.CONFLICT);
        }

        Course course = Course.builder()
                .courseCode(request.getCourseCode())
                .courseName(request.getCourseName())
                .credit(request.getCredit())
                .build();
        
        if (request.getLecturerId() != null) {
            User lecturer = userRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy giảng viên"));

            if (!"LECTURER".equals(lecturer.getRole().getRoleName())) {
                throw new CustomException("Người dùng được chọn không phải là Giảng viên", HttpStatus.BAD_REQUEST);
            }
            
            course.setLecturer(lecturer);
        }

        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy khóa học"));

        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setCredit(request.getCredit());
        
        if (request.getLecturerId() != null) {
            User lecturer = userRepository.findById(request.getLecturerId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy giảng viên"));
            
            // Kiểm tra xem User được gán có đúng là LECTURER không
            if (!"LECTURER".equals(lecturer.getRole().getRoleName())) {
                throw new CustomException("Người dùng được chọn không phải là Giảng viên", HttpStatus.BAD_REQUEST);
            }
            
            course.setLecturer(lecturer);
        }

        return CourseResponse.from(courseRepository.save(course));
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new NotFoundException("Không tìm thấy khóa học để xóa");
        }
        courseRepository.deleteById(id);
    }
}
