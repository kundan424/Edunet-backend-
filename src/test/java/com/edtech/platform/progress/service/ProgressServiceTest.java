package com.edtech.platform.progress.service;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.progress.dto.ProgressUpdateRequest;
import com.edtech.platform.progress.entity.LessonProgress;
import com.edtech.platform.progress.enums.ProgressStatus;
import com.edtech.platform.progress.repository.LessonProgressRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgressServiceTest {

    @Mock
    private LessonProgressRepository lessonProgressRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProgressService progressService;

    private UUID userId;
    private UUID courseId;
    private UUID lessonId;
    private Course course;
    private Lesson lesson;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        lessonId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        course = new Course();
        course.setId(courseId);

        Section section = new Section();
        section.setCourse(course);

        lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setSection(section);
        lesson.setLessonType(LessonType.VIDEO);

        ReflectionTestUtils.setField(progressService, "videoCompletionThreshold", 0.95);
    }

    @Test
    void updateProgress_NewProgress_VideoNotComplete() {
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        lesson.setDurationSeconds(100);

        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setPositionSeconds(50); // 50%

        progressService.updateProgress(userId, courseId, lessonId, request);

        ArgumentCaptor<LessonProgress> captor = ArgumentCaptor.forClass(LessonProgress.class);
        verify(lessonProgressRepository).save(captor.capture());

        LessonProgress saved = captor.getValue();
        assertEquals(50, saved.getLastPositionSeconds());
        assertEquals(50, saved.getMaxPositionSeconds());
        assertEquals(ProgressStatus.IN_PROGRESS, saved.getStatus());
    }

    @Test
    void updateProgress_VideoComplete() {
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)).thenReturn(Optional.empty());
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        lesson.setDurationSeconds(100);

        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setPositionSeconds(95); // 95% -> Complete

        progressService.updateProgress(userId, courseId, lessonId, request);

        ArgumentCaptor<LessonProgress> captor = ArgumentCaptor.forClass(LessonProgress.class);
        verify(lessonProgressRepository).save(captor.capture());

        LessonProgress saved = captor.getValue();
        assertEquals(95, saved.getLastPositionSeconds());
        assertEquals(95, saved.getMaxPositionSeconds());
        assertEquals(ProgressStatus.COMPLETED, saved.getStatus());
        assertNotNull(saved.getCompletedAt());
    }

    @Test
    void updateProgress_Rewind_DoesNotDecreaseMax() {
        when(enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)).thenReturn(true);
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        LessonProgress existing = new LessonProgress();
        existing.setLastPositionSeconds(80);
        existing.setMaxPositionSeconds(80);
        existing.setStatus(ProgressStatus.IN_PROGRESS);

        when(lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId)).thenReturn(Optional.of(existing));

        lesson.setDurationSeconds(100);

        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setPositionSeconds(50); // rewind to 50

        progressService.updateProgress(userId, courseId, lessonId, request);

        ArgumentCaptor<LessonProgress> captor = ArgumentCaptor.forClass(LessonProgress.class);
        verify(lessonProgressRepository).save(captor.capture());

        LessonProgress saved = captor.getValue();
        assertEquals(50, saved.getLastPositionSeconds()); // updated
        assertEquals(80, saved.getMaxPositionSeconds());  // preserved
        assertEquals(ProgressStatus.IN_PROGRESS, saved.getStatus());
    }
}
