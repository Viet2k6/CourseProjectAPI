package com.example.courseprojectapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialRequest {

    @NotBlank(message = "Tiêu đề tài liệu không được để trống")
    private String title;

    @NotNull(message = "ID khóa học không được để trống")
    private Long courseId;
}
