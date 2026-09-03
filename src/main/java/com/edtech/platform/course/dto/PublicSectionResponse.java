package com.edtech.platform.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicSectionResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer displayOrder;
    private List<PublicLessonResponse> lessons;
}
