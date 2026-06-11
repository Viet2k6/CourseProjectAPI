package com.example.courseprojectapi.model.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequest {

    private String githubUrl;

    private String reportUrl;
}