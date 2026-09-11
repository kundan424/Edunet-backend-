package com.edtech.platform.course.dto;
import lombok.Data;
import java.util.List;

@Data
public class SectionCurriculumResponse {
    private SectionResponse section;
    private List<LessonResponse> lessons;
}
