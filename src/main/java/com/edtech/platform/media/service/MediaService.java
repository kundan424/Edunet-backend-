package com.edtech.platform.media.service;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.media.domain.MediaAsset;
import com.edtech.platform.media.domain.ProcessingStatus;
import com.edtech.platform.media.repository.MediaAssetRepository;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.VerificationStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaStorageService mediaStorageService;
    private final MediaAssetRepository mediaAssetRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    public MediaService(MediaStorageService mediaStorageService, MediaAssetRepository mediaAssetRepository,
                        LessonRepository lessonRepository, CourseRepository courseRepository,
                        InstructorProfileRepository instructorProfileRepository) {
        this.mediaStorageService = mediaStorageService;
        this.mediaAssetRepository = mediaAssetRepository;
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.instructorProfileRepository = instructorProfileRepository;
    }

    @Transactional
    public MediaAsset uploadVideo(UUID instructorId, UUID courseId, UUID lessonId, MultipartFile file) {
        InstructorProfile profile = instructorProfileRepository.findByUserId(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));
        
        if (profile.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new SecurityException("Only verified instructors can upload media");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
                
        if (!course.getInstructorId().equals(instructorId)) {
            throw new SecurityException("Instructor does not own this course");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));
                
        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to this course");
        }

        if (lesson.getLessonType() != LessonType.VIDEO) {
            throw new IllegalArgumentException("Media can only be uploaded to VIDEO lessons");
        }

        // Delete existing media asset if any
        mediaAssetRepository.findByLessonId(lessonId).ifPresent(existingAsset -> {
            try {
                mediaStorageService.delete(existingAsset.getStorageKey());
                mediaAssetRepository.delete(existingAsset);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete existing media", e);
            }
        });

        try {
            String storageKey = mediaStorageService.store(file);
            MediaAsset asset = new MediaAsset();
            asset.setLesson(lesson);
            asset.setOriginalFileName(file.getOriginalFilename());
            asset.setStorageKey(storageKey);
            asset.setContentType(file.getContentType() != null ? file.getContentType() : "video/mp4");
            asset.setFileSize(file.getSize());
            asset.setProcessingStatus(ProcessingStatus.READY); // Immediate for local
            // durationSeconds omitted or mock value
            asset.setDurationSeconds(0);
            
            return mediaAssetRepository.save(asset);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store media", e);
        }
    }

    @Transactional
    public void deleteVideo(UUID instructorId, UUID courseId, UUID lessonId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
                
        if (!course.getInstructorId().equals(instructorId)) {
            throw new SecurityException("Instructor does not own this course");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Lesson does not belong to this course");
        }

        mediaAssetRepository.findByLessonId(lessonId).ifPresent(asset -> {
            try {
                mediaStorageService.delete(asset.getStorageKey());
                mediaAssetRepository.delete(asset);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete media", e);
            }
        });
    }
    
    public MediaAsset getMediaAsset(UUID lessonId) {
        return mediaAssetRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("No media found for this lesson"));
    }

    public Resource getResource(String storageKey) throws IOException {
        return mediaStorageService.getResource(storageKey);
    }
}
