package com.edtech.platform.course.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.SectionRequest;
import com.edtech.platform.course.dto.SectionResponse;
import com.edtech.platform.course.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.edtech.platform.course.dto.SectionReorderRequest;

import com.edtech.platform.common.response.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sectionService.createSection(userDetails.getId(), courseId, request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getSections(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.getSectionsByCourse(userDetails.getId(), courseId)));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(sectionService.updateSection(userDetails.getId(), courseId, sectionId, request)));
    }

    @PatchMapping("/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderSections(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @Valid @RequestBody SectionReorderRequest request) {
        sectionService.reorderSections(userDetails.getId(), courseId, request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        sectionService.deleteSection(userDetails.getId(), courseId, sectionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
