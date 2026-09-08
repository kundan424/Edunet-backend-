package com.edtech.platform.progress.service;

import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.progress.dto.CourseProgressResponse;
import com.edtech.platform.progress.dto.LessonProgressResponse;
import com.edtech.platform.progress.dto.ProgressUpdateRequest;
import com.edtech.platform.progress.entity.LessonProgress;
import com.edtech.platform.progress.enums.ProgressStatus;
import com.edtech.platform.progress.repository.LessonProgressRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Value("${progress.video.completion-threshold:0.95}")
    private double videoCompletionThreshold;

    @Transactional
    public void updateProgress(UUID userId, UUID courseId, UUID lessonId, ProgressUpdateRequest request) {
        // 1. Verify enrollment
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (!isEnrolled) {
            throw new org.springframework.security.access.AccessDeniedException("User is not enrolled in this course");
        }

        // 2. Verify course and lesson
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new com.edtech.platform.common.exception.EdTechException(
                    com.edtech.platform.common.exception.ErrorCode.VALIDATION_FAILED, 
                    "Lesson does not belong to the specified course");
        }

        // 3. Find or create progress
        LessonProgress progress = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    User user = userRepository.getReferenceById(userId);
                    newProgress.setUser(user);
                    newProgress.setCourse(course);
                    newProgress.setLesson(lesson);
                    newProgress.setStatus(ProgressStatus.NOT_STARTED);
                    return newProgress;
                });

        // 4. Update positions
        int submittedPosition = request.getPositionSeconds() != null ? request.getPositionSeconds() : 0;
        
        if (submittedPosition < 0) {
            submittedPosition = 0;
        }

        progress.setLastPositionSeconds(submittedPosition);
        progress.setMaxPositionSeconds(Math.max(progress.getMaxPositionSeconds(), submittedPosition));

        // 5. Completion logic
        if (progress.getStatus() != ProgressStatus.COMPLETED) {
            boolean isCompleted = false;
            
            if (lesson.getLessonType() == LessonType.VIDEO) {
                if (lesson.getDurationSeconds() != null && lesson.getDurationSeconds() > 0) {
                    double watchedRatio = (double) progress.getMaxPositionSeconds() / lesson.getDurationSeconds();
                    if (watchedRatio >= videoCompletionThreshold) {
                        isCompleted = true;
                    }
                }
            } else if (lesson.getLessonType() == LessonType.QUIZ) {
                // Quizzes are explicitly marked complete upon attempt submission
                isCompleted = false;
            } else {
                // Non-video lessons (TEXT, etc.) are completed simply by recording a progress update
                isCompleted = true;
            }

            if (isCompleted) {
                progress.setStatus(ProgressStatus.COMPLETED);
                progress.setCompletedAt(LocalDateTime.now());
            } else {
                progress.setStatus(ProgressStatus.IN_PROGRESS);
            }
        }

        lessonProgressRepository.save(progress);
    }

    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgress(UUID userId, UUID courseId) {
        // Verify enrollment
        boolean isEnrolled = enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
        if (!isEnrolled) {
            throw new org.springframework.security.access.AccessDeniedException("User is not enrolled in this course");
        }

        List<LessonProgress> progresses = lessonProgressRepository.findByUserIdAndCourseId(userId, courseId);
        
        int totalLessons = lessonRepository.countBySectionCourseId(courseId);
        int completedLessons = (int) progresses.stream()
                .filter(p -> p.getStatus() == ProgressStatus.COMPLETED)
                .count();

        double completionPercentage = 0.0;
        if (totalLessons > 0) {
            completionPercentage = Math.round(((double) completedLessons / totalLessons) * 100.0);
        }

        UUID lastAccessedLessonId = null;
        LocalDateTime lastAccessedAt = null;

        for (LessonProgress p : progresses) {
            if (lastAccessedAt == null || p.getUpdatedAt().isAfter(lastAccessedAt)) {
                lastAccessedAt = p.getUpdatedAt();
                lastAccessedLessonId = p.getLesson().getId();
            }
        }

        List<LessonProgressResponse> lessonResponses = progresses.stream()
                .map(p -> LessonProgressResponse.builder()
                        .lessonId(p.getLesson().getId())
                        .status(p.getStatus())
                        .lastPositionSeconds(p.getLastPositionSeconds())
                        .maxPositionSeconds(p.getMaxPositionSeconds())
                        .completedAt(p.getCompletedAt())
                        .build())
                .collect(Collectors.toList());

        return CourseProgressResponse.builder()
                .courseId(courseId)
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .completionPercentage(completionPercentage)
                .lastAccessedLessonId(lastAccessedLessonId)
                .lastAccessedAt(lastAccessedAt)
                .lessonProgress(lessonResponses)
                .build();
    }

    @Transactional
    public void completeLesson(UUID userId, UUID courseId, UUID lessonId) {
        LessonProgress progress = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> {
                    LessonProgress newProgress = new LessonProgress();
                    User user = userRepository.getReferenceById(userId);
                    Course course = courseRepository.getReferenceById(courseId);
                    Lesson lesson = lessonRepository.getReferenceById(lessonId);
                    newProgress.setUser(user);
                    newProgress.setCourse(course);
                    newProgress.setLesson(lesson);
                    newProgress.setLastPositionSeconds(0);
                    newProgress.setMaxPositionSeconds(0);
                    return newProgress;
                });

        if (progress.getStatus() != ProgressStatus.COMPLETED) {
            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(progress);
        }
    }
}
