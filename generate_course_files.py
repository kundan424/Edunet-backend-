import os

BASE_DIR = r"E:\projects\E-learning\src\main"
JAVA_BASE = os.path.join(BASE_DIR, "java", "com", "edtech", "platform")
MIG_DIR = os.path.join(BASE_DIR, "resources", "db", "migration")

course_pkg = os.path.join(JAVA_BASE, "course")
course_entity_pkg = os.path.join(course_pkg, "entity")
course_enum_pkg = os.path.join(course_pkg, "enums")
course_repo_pkg = os.path.join(course_pkg, "repository")
course_service_pkg = os.path.join(course_pkg, "service")
course_controller_pkg = os.path.join(course_pkg, "controller")
course_dto_pkg = os.path.join(course_pkg, "dto")
course_exception_pkg = os.path.join(course_pkg, "exception")

dirs = [course_pkg, course_entity_pkg, course_enum_pkg, course_repo_pkg, course_service_pkg, course_controller_pkg, course_dto_pkg, course_exception_pkg]
for d in dirs:
    os.makedirs(d, exist_ok=True)

# 1. Migrations
with open(os.path.join(MIG_DIR, "V4__create_courses_table.sql"), "w") as f:
    f.write("""CREATE TABLE courses (
    id UUID PRIMARY KEY,
    instructor_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    difficulty VARCHAR(50),
    price DECIMAL(10, 2),
    thumbnail_url VARCHAR(255),
    publish_status VARCHAR(50) NOT NULL,
    rating DECIMAL(3, 2),
    student_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
""")

with open(os.path.join(MIG_DIR, "V5__create_sections_table.sql"), "w") as f:
    f.write("""CREATE TABLE sections (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    display_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_sections_course_id_order ON sections(course_id, display_order);
""")

with open(os.path.join(MIG_DIR, "V6__create_lessons_table.sql"), "w") as f:
    f.write("""CREATE TABLE lessons (
    id UUID PRIMARY KEY,
    section_id UUID NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    lesson_type VARCHAR(50) NOT NULL,
    display_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_lessons_section_id_order ON lessons(section_id, display_order);
""")

# 2. Enums
with open(os.path.join(course_enum_pkg, "CourseDifficulty.java"), "w") as f:
    f.write("""package com.edtech.platform.course.enums;
public enum CourseDifficulty { BEGINNER, INTERMEDIATE, ADVANCED }
""")

with open(os.path.join(course_enum_pkg, "PublishStatus.java"), "w") as f:
    f.write("""package com.edtech.platform.course.enums;
public enum PublishStatus { DRAFT, PENDING_APPROVAL, PUBLISHED, ARCHIVED }
""")

with open(os.path.join(course_enum_pkg, "LessonType.java"), "w") as f:
    f.write("""package com.edtech.platform.course.enums;
public enum LessonType { VIDEO, TEXT, QUIZ, ASSIGNMENT, RESOURCE }
""")

# 3. Entities
with open(os.path.join(course_entity_pkg, "Course.java"), "w") as f:
    f.write("""package com.edtech.platform.course.entity;

import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.PublishStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "instructor_id", nullable = false)
    private UUID instructorId;

    @Column(nullable = false)
    private String title;

    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    private CourseDifficulty difficulty;

    private BigDecimal price;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "publish_status", nullable = false)
    private PublishStatus publishStatus = PublishStatus.DRAFT;

    private BigDecimal rating;

    @Column(name = "student_count")
    private Integer studentCount = 0;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
""")

with open(os.path.join(course_entity_pkg, "Section.java"), "w") as f:
    f.write("""package com.edtech.platform.course.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sections")
@Getter
@Setter
public class Section {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
""")

with open(os.path.join(course_entity_pkg, "Lesson.java"), "w") as f:
    f.write("""package com.edtech.platform.course.entity;

import com.edtech.platform.course.enums.LessonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessons")
@Getter
@Setter
public class Lesson {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", nullable = false)
    private LessonType lessonType;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
""")

# 4. Exceptions
with open(os.path.join(course_exception_pkg, "CourseOwnershipException.java"), "w") as f:
    f.write("""package com.edtech.platform.course.exception;

public class CourseOwnershipException extends RuntimeException {
    public CourseOwnershipException(String message) {
        super(message);
    }
}
""")

with open(os.path.join(course_exception_pkg, "CourseNotPublishableException.java"), "w") as f:
    f.write("""package com.edtech.platform.course.exception;

public class CourseNotPublishableException extends RuntimeException {
    public CourseNotPublishableException(String message) {
        super(message);
    }
}
""")

with open(os.path.join(course_exception_pkg, "ResourceNotFoundException.java"), "w") as f:
    f.write("""package com.edtech.platform.course.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
""")

# 5. DTOs
with open(os.path.join(course_dto_pkg, "CourseCreateRequest.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseCreateRequest {
    @NotBlank
    private String title;
    private String description;
    private String category;
    private CourseDifficulty difficulty;
    @DecimalMin("0.0")
    private BigDecimal price;
    private String thumbnailUrl;
}
""")

with open(os.path.join(course_dto_pkg, "CourseUpdateRequest.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CourseUpdateRequest {
    @NotBlank
    private String title;
    private String description;
    private String category;
    private CourseDifficulty difficulty;
    @DecimalMin("0.0")
    private BigDecimal price;
    private String thumbnailUrl;
}
""")

with open(os.path.join(course_dto_pkg, "SectionRequest.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private Integer displayOrder;
}
""")

with open(os.path.join(course_dto_pkg, "LessonRequest.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank
    private String title;
    private String description;
    @NotNull
    private LessonType lessonType;
    @NotNull
    private Integer displayOrder;
}
""")

with open(os.path.join(course_dto_pkg, "CourseResponse.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.CourseDifficulty;
import com.edtech.platform.course.enums.PublishStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private UUID instructorId;
    private String title;
    private String description;
    private String category;
    private CourseDifficulty difficulty;
    private BigDecimal price;
    private String thumbnailUrl;
    private PublishStatus publishStatus;
    private BigDecimal rating;
    private Integer studentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
""")

with open(os.path.join(course_dto_pkg, "SectionResponse.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SectionResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
""")

with open(os.path.join(course_dto_pkg, "LessonResponse.java"), "w") as f:
    f.write("""package com.edtech.platform.course.dto;

import com.edtech.platform.course.enums.LessonType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class LessonResponse {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String description;
    private LessonType lessonType;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
""")

# 6. Repositories
with open(os.path.join(course_repo_pkg, "CourseRepository.java"), "w") as f:
    f.write("""package com.edtech.platform.course.repository;

import com.edtech.platform.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByInstructorId(UUID instructorId);
}
""")

with open(os.path.join(course_repo_pkg, "SectionRepository.java"), "w") as f:
    f.write("""package com.edtech.platform.course.repository;

import com.edtech.platform.course.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {
    List<Section> findByCourseIdOrderByDisplayOrderAsc(UUID courseId);
}
""")

with open(os.path.join(course_repo_pkg, "LessonRepository.java"), "w") as f:
    f.write("""package com.edtech.platform.course.repository;

import com.edtech.platform.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findBySectionIdOrderByDisplayOrderAsc(UUID sectionId);
}
""")

# 7. Services
with open(os.path.join(course_service_pkg, "CourseService.java"), "w") as f:
    f.write("""package com.edtech.platform.course.service;

import com.edtech.platform.course.dto.CourseCreateRequest;
import com.edtech.platform.course.dto.CourseResponse;
import com.edtech.platform.course.dto.CourseUpdateRequest;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.enums.PublishStatus;
import com.edtech.platform.course.exception.CourseNotPublishableException;
import com.edtech.platform.course.exception.CourseOwnershipException;
import com.edtech.platform.course.exception.ResourceNotFoundException;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.user.entity.InstructorProfile;
import com.edtech.platform.user.enums.VerificationStatus;
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
                .orElseThrow(() -> new CourseNotPublishableException("Instructor profile not found"));
        
        if (profile.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new CourseNotPublishableException("Instructor must be VERIFIED to publish courses");
        }

        Course course = getCourseAndVerifyOwnership(instructorId, courseId);
        
        if (course.getSections().isEmpty()) {
            throw new CourseNotPublishableException("Course must have at least one section to be published");
        }
        
        boolean hasLesson = course.getSections().stream()
                .anyMatch(section -> !section.getLessons().isEmpty());
                
        if (!hasLesson) {
            throw new CourseNotPublishableException("Course must have at least one lesson to be published");
        }

        course.setPublishStatus(PublishStatus.PENDING_APPROVAL);
        courseRepository.save(course);
    }

    public Course getCourseAndVerifyOwnership(UUID instructorId, UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (!course.getInstructorId().equals(instructorId)) {
            throw new CourseOwnershipException("You do not own this course");
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
""")

with open(os.path.join(course_service_pkg, "SectionService.java"), "w") as f:
    f.write("""package com.edtech.platform.course.service;

import com.edtech.platform.course.dto.SectionRequest;
import com.edtech.platform.course.dto.SectionResponse;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.exception.ResourceNotFoundException;
import com.edtech.platform.course.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final CourseService courseService;

    @Transactional
    public SectionResponse createSection(UUID instructorId, UUID courseId, SectionRequest request) {
        Course course = courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = new Section();
        section.setCourse(course);
        section.setTitle(request.getTitle());
        section.setDescription(request.getDescription());
        section.setDisplayOrder(request.getDisplayOrder());
        section = sectionRepository.save(section);
        return mapToResponse(section);
    }

    @Transactional(readOnly = true)
    public List<SectionResponse> getSectionsByCourse(UUID instructorId, UUID courseId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        return sectionRepository.findByCourseIdOrderByDisplayOrderAsc(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SectionResponse updateSection(UUID instructorId, UUID courseId, UUID sectionId, SectionRequest request) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = getSection(sectionId, courseId);
        section.setTitle(request.getTitle());
        section.setDescription(request.getDescription());
        section.setDisplayOrder(request.getDisplayOrder());
        section = sectionRepository.save(section);
        return mapToResponse(section);
    }

    @Transactional
    public void deleteSection(UUID instructorId, UUID courseId, UUID sectionId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = getSection(sectionId, courseId);
        sectionRepository.delete(section);
    }

    public Section getSection(UUID sectionId, UUID courseId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        if (!section.getCourse().getId().equals(courseId)) {
            throw new ResourceNotFoundException("Section does not belong to the specified course");
        }
        return section;
    }

    private SectionResponse mapToResponse(Section section) {
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
}
""")

with open(os.path.join(course_service_pkg, "LessonService.java"), "w") as f:
    f.write("""package com.edtech.platform.course.service;

import com.edtech.platform.course.dto.LessonRequest;
import com.edtech.platform.course.dto.LessonResponse;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.entity.Section;
import com.edtech.platform.course.exception.ResourceNotFoundException;
import com.edtech.platform.course.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final SectionService sectionService;
    private final CourseService courseService;

    @Transactional
    public LessonResponse createLesson(UUID instructorId, UUID courseId, UUID sectionId, LessonRequest request) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Section section = sectionService.getSection(sectionId, courseId);
        Lesson lesson = new Lesson();
        lesson.setSection(section);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setLessonType(request.getLessonType());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson = lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsBySection(UUID instructorId, UUID courseId, UUID sectionId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        return lessonRepository.findBySectionIdOrderByDisplayOrderAsc(sectionId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LessonResponse updateLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId, LessonRequest request) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        Lesson lesson = getLesson(lessonId, sectionId);
        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setLessonType(request.getLessonType());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson = lessonRepository.save(lesson);
        return mapToResponse(lesson);
    }

    @Transactional
    public void deleteLesson(UUID instructorId, UUID courseId, UUID sectionId, UUID lessonId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        sectionService.getSection(sectionId, courseId);
        Lesson lesson = getLesson(lessonId, sectionId);
        lessonRepository.delete(lesson);
    }

    private Lesson getLesson(UUID lessonId, UUID sectionId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        if (!lesson.getSection().getId().equals(sectionId)) {
            throw new ResourceNotFoundException("Lesson does not belong to the specified section");
        }
        return lesson;
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setSectionId(lesson.getSection().getId());
        response.setTitle(lesson.getTitle());
        response.setDescription(lesson.getDescription());
        response.setLessonType(lesson.getLessonType());
        response.setDisplayOrder(lesson.getDisplayOrder());
        response.setCreatedAt(lesson.getCreatedAt());
        response.setUpdatedAt(lesson.getUpdatedAt());
        return response;
    }
}
""")

# 8. Controllers
with open(os.path.join(course_controller_pkg, "CourseController.java"), "w") as f:
    f.write("""package com.edtech.platform.course.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.CourseCreateRequest;
import com.edtech.platform.course.dto.CourseResponse;
import com.edtech.platform.course.dto.CourseUpdateRequest;
import com.edtech.platform.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CourseCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(userDetails.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(userDetails.getId()));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(courseService.getCourseById(userDetails.getId(), courseId));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(userDetails.getId(), courseId, request));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        courseService.deleteCourse(userDetails.getId(), courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/submit")
    public ResponseEntity<Void> submitForApproval(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        courseService.submitForApproval(userDetails.getId(), courseId);
        return ResponseEntity.ok().build();
    }
}
""")

with open(os.path.join(course_controller_pkg, "SectionController.java"), "w") as f:
    f.write("""package com.edtech.platform.course.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.SectionRequest;
import com.edtech.platform.course.dto.SectionResponse;
import com.edtech.platform.course.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sectionService.createSection(userDetails.getId(), courseId, request));
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getSections(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        return ResponseEntity.ok(sectionService.getSectionsByCourse(userDetails.getId(), courseId));
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody SectionRequest request) {
        return ResponseEntity.ok(sectionService.updateSection(userDetails.getId(), courseId, sectionId, request));
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        sectionService.deleteSection(userDetails.getId(), courseId, sectionId);
        return ResponseEntity.noContent().build();
    }
}
""")

with open(os.path.join(course_controller_pkg, "LessonController.java"), "w") as f:
    f.write("""package com.edtech.platform.course.controller;

import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.course.dto.LessonRequest;
import com.edtech.platform.course.dto.LessonResponse;
import com.edtech.platform.course.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/sections/{sectionId}/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonResponse> createLesson(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(lessonService.createLesson(userDetails.getId(), courseId, sectionId, request));
    }

    @GetMapping
    public ResponseEntity<List<LessonResponse>> getLessons(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId) {
        return ResponseEntity.ok(lessonService.getLessonsBySection(userDetails.getId(), courseId, sectionId));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(userDetails.getId(), courseId, sectionId, lessonId, request));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID sectionId,
            @PathVariable UUID lessonId) {
        lessonService.deleteLesson(userDetails.getId(), courseId, sectionId, lessonId);
        return ResponseEntity.noContent().build();
    }
}
""")

# 9. Exception Handler
with open(os.path.join(course_exception_pkg, "CourseExceptionHandler.java"), "w") as f:
    f.write("""package com.edtech.platform.course.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CourseExceptionHandler {

    @ExceptionHandler(CourseOwnershipException.class)
    public ResponseEntity<Map<String, String>> handleCourseOwnershipException(CourseOwnershipException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(CourseNotPublishableException.class)
    public ResponseEntity<Map<String, String>> handleCourseNotPublishableException(CourseNotPublishableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
""")
