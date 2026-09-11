package com.edtech.platform.course.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.LessonRequest;
import com.edtech.platform.course.dto.LessonResponse;
import com.edtech.platform.course.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.edtech.platform.course.dto.LessonReorderRequest;

import com.edtech.platform.common.response.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/sections/{sectionId}/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(lessonService.createLesson(userDetails.getId(), courseId, sectionId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessons(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        return ResponseEntity.ok(ApiResponse.success(lessonService.getLessonsBySection(userDetails.getId(), courseId, sectionId)));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(ApiResponse.success(lessonService.updateLesson(userDetails.getId(), courseId, sectionId, lessonId, request)));
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderLessons(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody LessonReorderRequest request) {
        lessonService.reorderLessons(userDetails.getId(), courseId, sectionId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @PathVariable UUID lessonId) {
        lessonService.deleteLesson(userDetails.getId(), courseId, sectionId, lessonId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
