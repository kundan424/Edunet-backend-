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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Transactional
    public CourseResponse createCourse(UUID instructorId, CourseCreateRequest request) {
        Course course = new Course();
        course.setInstructorId(instructorId);
        course.setTitle(request.getTitle());
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
