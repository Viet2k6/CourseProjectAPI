package com.example.courseprojectapi.model.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponse {

    private Long id;

    private String title;

    private String fileUrl;
}
