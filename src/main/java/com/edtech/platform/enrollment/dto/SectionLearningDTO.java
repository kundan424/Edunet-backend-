package com.edtech.platform.enrollment.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SectionLearningDTO {
    private UUID id;
    private String title;
    private Integer displayOrder;
    private List<LessonLearningDTO> lessons;
}
