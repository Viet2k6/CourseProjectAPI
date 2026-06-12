package com.example.courseprojectapi.model.dto.response;

import com.example.courseprojectapi.model.entity.Submission;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private String githubUrl;
    private String status;
    private Double score;

    public static SubmissionResponse from(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .githubUrl(submission.getGithubUrl())
                .status(submission.getStatus())
                .score(submission.getScore())
                .build();
    }
}
