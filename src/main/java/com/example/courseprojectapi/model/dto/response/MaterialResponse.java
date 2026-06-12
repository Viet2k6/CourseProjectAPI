package com.example.courseprojectapi.model.dto.response;

import com.example.courseprojectapi.model.entity.Material;
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

    public static MaterialResponse from(Material material) {
        return MaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .fileUrl(material.getFileUrl())
                .build();
    }
}
