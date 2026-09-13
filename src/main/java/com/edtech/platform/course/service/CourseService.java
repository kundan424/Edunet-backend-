package com.edtech.platform.course.service;

import com.edtech.platform.course.dto.CourseCreateRequest;
import com.edtech.platform.course.dto.CourseResponse;
import com.edtech.platform.course.dto.CourseUpdateRequest;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.entity.VerificationStatus;
import com.edtech.platform.user.repository.InstructorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.edtech.platform.course.dto.CourseCurriculumResponse;
import com.edtech.platform.course.dto.SectionCurriculumResponse;
import com.edtech.platform.course.dto.SectionResponse;
import com.edtech.platform.course.dto.LessonResponse;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.repository.SectionRepository;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.payment.repository.PaymentRepository;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;


    @Transactional
    public CourseResponse createCourse(UUID instructorId, CourseCreateRequest request) {
        Course course = new Course();
        course.setInstructorId(instructorId);
        course.setTitle(request.getTitle());
        if (course.getPublishStatus() == PublishStatus.PUBLISHED) {
            course.setPublishStatus(PublishStatus.PENDING_APPROVAL);
        }
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDifficulty(request.getDifficulty());
        course.setPrice(request.getPrice());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByInstructor(UUID instructorId) {
        return courseRepository.findByInstructorId(instructorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(UUID instructorId, UUID courseId) {
        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        return mapToResponse(course);
    }

    @Transactional
    public CourseResponse updateCourse(UUID instructorId, UUID courseId, CourseUpdateRequest request) {
        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        course.setTitle(request.getTitle());
        if (course.getPublishStatus() == PublishStatus.PUBLISHED) {
            course.setPublishStatus(PublishStatus.PENDING_APPROVAL);
        }
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setDifficulty(request.getDifficulty());
        course.setPrice(request.getPrice());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course = courseRepository.save(course);
        return mapToResponse(course);
    }

    
    @Transactional
    public void deleteCourse(UUID instructorId, UUID courseId) {
        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        
        if (enrollmentRepository.existsByCourseId(courseId) || paymentRepository.existsByCourseId(courseId)) {
            throw new EdTechException(ErrorCode.INVALID_COURSE_STATE_TRANSITION, "Cannot delete a course with active enrollments or payments. Please archive it instead.");
        }
        
        courseRepository.delete(course);
    }


    @Transactional
    public void submitForApproval(UUID instructorId, UUID courseId) {
        InstructorProfile profile = instructorProfileRepository.findByUserId(instructorId)
                .orElseThrow(() -> new EdTechException(ErrorCode.INSTRUCTOR_NOT_VERIFIED, "Instructor onboarding/profile creation is required before submitting a course"));

        
        if (profile.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new EdTechException(ErrorCode.INSTRUCTOR_NOT_VERIFIED, "Instructor must be VERIFIED to publish courses");
        }

        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        
        if (course.getSections().isEmpty()) {
            throw new EdTechException(ErrorCode.INVALID_COURSE_STATE_TRANSITION, "Course must have at least one section to be published");
        }
        
        boolean hasLesson = course.getSections().stream()
                .anyMatch(section -> !section.getLessons().isEmpty());
                
        if (!hasLesson) {
            throw new EdTechException(ErrorCode.INVALID_COURSE_STATE_TRANSITION, "Course must have at least one lesson to be published");
        }

        course.setPublishStatus(PublishStatus.PENDING_APPROVAL);
        courseRepository.save(course);
    }

    
    @Transactional
    public void archiveCourse(UUID instructorId, UUID courseId) {
        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        course.setPublishStatus(PublishStatus.ARCHIVED);
        courseRepository.save(course);
    }

    @Transactional
    public void unarchiveCourse(UUID instructorId, UUID courseId) {
        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        if (course.getPublishStatus() != PublishStatus.ARCHIVED) {
            throw new EdTechException(ErrorCode.INVALID_COURSE_STATE_TRANSITION, "Course is not archived");
        }
        course.setPublishStatus(PublishStatus.DRAFT);
        courseRepository.save(course);
    }

    @Transactional(readOnly = true)
    public CourseCurriculumResponse getCourseCurriculum(UUID instructorId, UUID courseId) {
        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        return buildCourseCurriculumResponse(course);
    }

    public CourseCurriculumResponse buildCourseCurriculumResponse(Course course) {
        List<Section> sections = sectionRepository.findByCourseIdOrderByDisplayOrderAsc(course.getId());
        
        List<UUID> sectionIds = sections.stream().map(Section::getId).collect(Collectors.toList());
        List<Lesson> allLessons = sectionIds.isEmpty() ? new ArrayList<>() : lessonRepository.findBySectionIdInOrderByDisplayOrderAsc(sectionIds);
        
        Map<UUID, List<Lesson>> lessonsBySection = allLessons.stream().collect(Collectors.groupingBy(l -> l.getSection().getId()));
        
        CourseCurriculumResponse response = new CourseCurriculumResponse();
        response.setCourse(mapToResponse(course));
        
        List<SectionCurriculumResponse> sectionResponses = new ArrayList<>();
        for (Section section : sections) {
            SectionCurriculumResponse scr = new SectionCurriculumResponse();
            scr.setSection(mapSectionToResponse(section));
            
            List<Lesson> lessons = lessonsBySection.getOrDefault(section.getId(), new ArrayList<>());
            scr.setLessons(lessons.stream().map(this::mapLessonToResponse).collect(Collectors.toList()));
            
            sectionResponses.add(scr);
        }
        response.setSections(sectionResponses);
        return response;
    }
    
    private SectionResponse mapSectionToResponse(Section section) {
        SectionResponse response = new SectionResponse();
        response.setId(section.getId());
        response.setCourseId(section.getCourse().getId());
        response.setTitle(section.getTitle());
        response.setDescription(section.getDescription());
        response.setDisplayOrder(section.getDisplayOrder());
        response.setCreatedAt(section.getCreatedAt());
        response.setUpdatedAt(section.getUpdatedAt());
        return response;
    }
    
    private LessonResponse mapLessonToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setSectionId(lesson.getSection().getId());
        response.setTitle(lesson.getTitle());
        response.setDescription(lesson.getDescription());
        response.setLessonType(lesson.getLessonType());
        response.setDisplayOrder(lesson.getDisplayOrder());
        response.setDurationSeconds(lesson.getDurationSeconds());
        response.setCreatedAt(lesson.getCreatedAt());
        response.setUpdatedAt(lesson.getUpdatedAt());
        return response;
    }

    // Ensures any structural change to PUBLISHED course downgrades it
    public void downgradeIfPublished(Course course) {
        if (course.getPublishStatus() == PublishStatus.PUBLISHED) {
            course.setPublishStatus(PublishStatus.PENDING_APPROVAL);
            courseRepository.save(course);
        }
    }

    public Course getCourseAndVerifyOwnership(UUID instructorId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EdTechException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new EdTechException(ErrorCode.COURSE_ACCESS_DENIED, "You do not own this course");
        }
        return course;
    }

    private CourseResponse mapToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setInstructorId(course.getInstructorId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setCategory(course.getCategory());
        response.setDifficulty(course.getDifficulty());
        response.setPrice(course.getPrice());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setPublishStatus(course.getPublishStatus());
        response.setRating(course.getRating());
        response.setStudentCount(course.getStudentCount());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        return response;
    }
}
