package com.example.courseprojectapi.controller;

import com.example.courseprojectapi.model.dto.request.MaterialRequest;
import com.example.courseprojectapi.model.dto.response.BaseResponse;
import com.example.courseprojectapi.service.LecturerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LECTURER')")
public class LecturerController {

    private final LecturerService lecturerService;
    private final ObjectMapper objectMapper;

    @PostMapping("/materials")
    public ResponseEntity<?> uploadMaterial(
            @RequestParam("data") String dataJson,
            @RequestParam("file") MultipartFile file) throws Exception {
        MaterialRequest request = objectMapper.readValue(dataJson, MaterialRequest.class);
        return new ResponseEntity<>(BaseResponse.success("Tải tài liệu lên thành công", lecturerService.uploadMaterial(request, file)), HttpStatus.CREATED);
    }

    @PostMapping("/grades/{submissionId}")
    public ResponseEntity<?> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Double score,
            @RequestParam String feedback) {
        lecturerService.gradeSubmission(submissionId, score, feedback);
        return ResponseEntity.ok(BaseResponse.success("Chấm điểm thành công", null));
    }

    @GetMapping("/courses/{courseId}/submissions")
    public ResponseEntity<?> getSubmissions(@PathVariable Long courseId) {
        return ResponseEntity.ok(BaseResponse.success("Lấy danh sách bài nộp thành công", lecturerService.getSubmissionsByCourse(courseId)));
    }
}
