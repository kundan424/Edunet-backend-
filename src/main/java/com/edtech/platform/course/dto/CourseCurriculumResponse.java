package com.edtech.platform.course.dto;
import lombok.Data;
import java.util.List;

@Data
public class CourseCurriculumResponse {
    private CourseResponse course;
    private List<SectionCurriculumResponse> sections;
}
