package com.edtech.platform.admin;

import com.edtech.platform.admin.dto.CourseRejectionRequest;
import com.edtech.platform.admin.service.AdminCourseModerationService;
import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCourseModerationServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private com.edtech.platform.course.service.CourseService courseService;

    @InjectMocks
    private AdminCourseModerationService moderationService;

    private Course course;
    private UUID adminId;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();
        course = new Course();
        course.setId(UUID.randomUUID());
        course.setInstructorId(UUID.randomUUID());
        course.setTitle("Test Course");
        course.setPublishStatus(PublishStatus.PENDING_APPROVAL);
    }

    @Test
    void testApproveCourse_Success() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        moderationService.approveCourse(course.getId(), adminId);

        assertEquals(PublishStatus.PUBLISHED, course.getPublishStatus());
        assertEquals(adminId, course.getReviewedBy());
        verify(courseRepository).save(course);
        
    }

    @Test
    void testApproveCourse_FailsIfNotPending() {
        course.setPublishStatus(PublishStatus.DRAFT);
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        assertThrows(EdTechException.class, () -> moderationService.approveCourse(course.getId(), adminId));
    }

    @Test
    void testRejectCourse_Success() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        CourseRejectionRequest request = new CourseRejectionRequest();
        request.setReason("Not good enough");

        moderationService.rejectCourse(course.getId(), adminId, request);

        assertEquals(PublishStatus.DRAFT, course.getPublishStatus());
        assertEquals(adminId, course.getReviewedBy());
        assertEquals("Not good enough", course.getRejectionReason());
        verify(courseRepository).save(course);
        
    }

    @Test
    void testGetCourseCurriculumForModeration_Success() {
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));
        
        com.edtech.platform.course.dto.CourseCurriculumResponse mockResponse = new com.edtech.platform.course.dto.CourseCurriculumResponse();
        when(courseService.buildCourseCurriculumResponse(course)).thenReturn(mockResponse);

        com.edtech.platform.course.dto.CourseCurriculumResponse response = moderationService.getCourseCurriculumForModeration(course.getId());

        assertNotNull(response);
        verify(courseService).buildCourseCurriculumResponse(course);
    }

    @Test
    void testGetCourseCurriculumForModeration_FailsIfNotPending() {
        course.setPublishStatus(PublishStatus.DRAFT);
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        assertThrows(EdTechException.class, () -> moderationService.getCourseCurriculumForModeration(course.getId()));
    }
}
