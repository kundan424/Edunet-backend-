package com.edtech.platform.media.service;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.media.domain.MediaAsset;
import com.edtech.platform.media.repository.MediaAssetRepository;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.VerificationStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaStorageService mediaStorageService;
    @Mock
    private MediaAssetRepository mediaAssetRepository;
    @Mock
    private LessonRepository lessonRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private InstructorProfileRepository instructorProfileRepository;

    @InjectMocks
    private MediaService mediaService;

    private UUID instructorId;
    private UUID courseId;
    private UUID lessonId;
    private InstructorProfile profile;
    private Course course;
    private Lesson lesson;
    private MultipartFile file;

    @BeforeEach
    void setUp() {
        instructorId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        lessonId = UUID.randomUUID();

        profile = new InstructorProfile();
        profile.setVerificationStatus(VerificationStatus.VERIFIED);

        course = new Course();
        course.setId(courseId);
        course.setInstructorId(instructorId);

        Section section = new Section();
        section.setCourse(course);

        lesson = new Lesson();
        lesson.setId(lessonId);
        lesson.setLessonType(LessonType.VIDEO);
        lesson.setSection(section);

        file = mock(MultipartFile.class);
    }

    @Test
    void uploadVideo_Success() throws IOException {
        when(instructorProfileRepository.findByUserId(instructorId)).thenReturn(Optional.of(profile));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(mediaAssetRepository.findByLessonId(lessonId)).thenReturn(Optional.empty());
        when(mediaStorageService.store(file)).thenReturn("test-key.mp4");
        when(file.getOriginalFilename()).thenReturn("video.mp4");
        when(file.getContentType()).thenReturn("video/mp4");
        when(file.getSize()).thenReturn(1024L);

        MediaAsset savedAsset = new MediaAsset();
        savedAsset.setStorageKey("test-key.mp4");
        when(mediaAssetRepository.save(any(MediaAsset.class))).thenReturn(savedAsset);

        MediaAsset result = mediaService.uploadVideo(instructorId, courseId, lessonId, file);

        assertNotNull(result);
        assertEquals("test-key.mp4", result.getStorageKey());
        verify(mediaAssetRepository).save(any(MediaAsset.class));
    }
}
