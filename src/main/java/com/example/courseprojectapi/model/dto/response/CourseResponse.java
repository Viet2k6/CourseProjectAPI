package com.example.courseprojectapi.model.dto.response;

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
}
