package com.edtech.platform.media.repository;

import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.course.repository.SectionRepository;
import com.edtech.platform.media.domain.MediaAsset;
import com.edtech.platform.media.domain.ProcessingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MediaAssetRepositoryTest {

    @Autowired
    private MediaAssetRepository mediaAssetRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private com.edtech.platform.user.repository.UserRepository userRepository;

    @Test
    void testSaveAndFind() {
        com.edtech.platform.user.entity.User user = new com.edtech.platform.user.entity.User();
        user.setEmail("instructor" + UUID.randomUUID() + "@test.com");
        user.setName("Test Instructor");
        user.setPasswordHash("hash");
        user.setRole(com.edtech.platform.user.entity.Role.INSTRUCTOR);
        user.setStatus(com.edtech.platform.user.entity.UserStatus.ACTIVE);
        user = userRepository.save(user);

        Course course = new Course();
        course.setInstructorId(user.getId());
        course.setTitle("Test Course");
        course = courseRepository.save(course);

        Section section = new Section();
        section.setCourse(course);
        section.setTitle("Test Section");
        section.setDisplayOrder(1);
        section = sectionRepository.save(section);

        Lesson lesson = new Lesson();
        lesson.setSection(section);
        lesson.setTitle("Test Lesson");
        lesson.setLessonType(LessonType.VIDEO);
        lesson.setDisplayOrder(1);
        lesson = lessonRepository.save(lesson);

        MediaAsset asset = new MediaAsset();
        asset.setLesson(lesson);
        asset.setOriginalFileName("test.mp4");
        asset.setStorageKey("test-key.mp4");
        asset.setContentType("video/mp4");
        asset.setFileSize(100L);
        asset.setProcessingStatus(ProcessingStatus.READY);
        mediaAssetRepository.save(asset);

        Optional<MediaAsset> found = mediaAssetRepository.findByLessonId(lesson.getId());
        assertTrue(found.isPresent());
    }
}
