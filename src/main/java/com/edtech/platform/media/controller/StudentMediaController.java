package com.edtech.platform.media.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.enrollment.entity.Enrollment;
import com.edtech.platform.enrollment.enums.EnrollmentStatus;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.media.domain.MediaAsset;
import com.edtech.platform.media.domain.ProcessingStatus;
import com.edtech.platform.media.service.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/lessons/{lessonId}/media")
public class StudentMediaController {

    private final MediaService mediaService;
    private final EnrollmentRepository enrollmentRepository;

    public StudentMediaController(MediaService mediaService, EnrollmentRepository enrollmentRepository) {
        this.mediaService = mediaService;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping
    public ResponseEntity<Resource> getMedia(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId) throws IOException {

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userDetails.getId(), courseId)
                .orElseThrow(() -> new SecurityException("User is not enrolled in this course"));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {
            throw new SecurityException("Enrollment is not active");
        }

        MediaAsset asset = mediaService.getMediaAsset(lessonId);

        if (asset.getProcessingStatus() != ProcessingStatus.READY) {
            throw new IllegalStateException("Media is not ready for viewing yet");
        }

        Resource resource = mediaService.getResource(asset.getStorageKey());
        
        String contentType = asset.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + asset.getOriginalFileName() + "\"")
                .body(resource);
    }
}
