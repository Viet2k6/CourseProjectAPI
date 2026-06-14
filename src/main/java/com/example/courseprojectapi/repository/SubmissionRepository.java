package com.example.courseprojectapi.repository;

import com.example.courseprojectapi.model.entity.Course;
import com.example.courseprojectapi.model.entity.Submission;
import com.example.courseprojectapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByStudentAndCourse(User student, Course course);
}
