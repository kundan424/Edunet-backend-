package com.edtech.platform.course.dto;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class LessonReorderRequest {
    @NotEmpty(message = "Ordered lesson IDs list cannot be empty")
    private List<UUID> orderedLessonIds;
}
