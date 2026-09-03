package com.edtech.platform.enrollment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class CourseLearningResponse {
    private UUID id;
    private String title;
    private String description;
    private List<SectionLearningDTO> sections;
}
