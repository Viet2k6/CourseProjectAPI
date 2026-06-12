package com.example.courseprojectapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionRequest {
    @NotBlank(message = "GitHub URL không được để trống")
    private String githubUrl;
    
    private Long courseId;
}
