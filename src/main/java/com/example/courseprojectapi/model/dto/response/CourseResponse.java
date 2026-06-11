package com.example.courseprojectapi.model.dto.response;

import com.example.courseprojectapi.model.entity.Course;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private Integer credit;

    public static CourseResponse from(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .courseCode(course.getCourseCode())
                .courseName(course.getCourseName())
                .credit(course.getCredit())
                .build();
    }
}
