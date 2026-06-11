package com.example.courseprojectapi.repository;

import com.example.courseprojectapi.model.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Boolean existsByCourseCode(String courseCode);

    @Query("select c from Course c where " +
           "(:search is null or lower(c.courseName) like lower(concat('%', :search, '%')) or " +
           "lower(c.courseCode) like lower(concat('%', :search, '%')))")
    Page<Course> searchCourses(@Param("search") String search, Pageable pageable);
}
