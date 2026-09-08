package com.edtech.platform.media.controller;

import com.edtech.platform.media.domain.MediaAsset;
import com.edtech.platform.media.dto.MediaAssetDto;
import com.edtech.platform.media.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.edtech.platform.common.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/lessons/{lessonId}/media")
public class InstructorMediaController {

    private final MediaService mediaService;

    public InstructorMediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping
    public ResponseEntity<MediaAssetDto> uploadMedia(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @RequestParam("file") MultipartFile file) {
        
        UUID instructorId = userDetails.getId();
        MediaAsset asset = mediaService.uploadVideo(instructorId, courseId, lessonId, file);
        return ResponseEntity.ok(mapToDto(asset));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMedia(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId) {
        
        UUID instructorId = userDetails.getId();
        mediaService.deleteVideo(instructorId, courseId, lessonId);
        return ResponseEntity.noContent().build();
    }
    
    private MediaAssetDto mapToDto(MediaAsset asset) {
        return MediaAssetDto.builder()
                .id(asset.getId())
                .lessonId(asset.getLesson().getId())
                .originalFileName(asset.getOriginalFileName())
                .contentType(asset.getContentType())
                .fileSize(asset.getFileSize())
                .durationSeconds(asset.getDurationSeconds())
                .processingStatus(asset.getProcessingStatus())
                .createdAt(asset.getCreatedAt())
                .updatedAt(asset.getUpdatedAt())
                .build();
    }
}
