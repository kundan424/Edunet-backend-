package com.edtech.platform.course.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.dto.CourseReviewRequest;
import com.edtech.platform.course.dto.CourseReviewResponse;
import com.edtech.platform.course.entity.Course;
import com.edtech.platform.course.entity.CourseReview;
import com.edtech.platform.course.repository.CourseRepository;
import com.edtech.platform.course.repository.CourseReviewRepository;
import com.edtech.platform.enrollment.enums.EnrollmentStatus;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseReviewService {

    private final CourseReviewRepository courseReviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CourseReviewResponse createReview(UUID userId, UUID courseId, CourseReviewRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EdTechException(ErrorCode.COURSE_NOT_FOUND, "Course not found"));

        if (!enrollmentRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, EnrollmentStatus.ACTIVE)) {
            throw new EdTechException(ErrorCode.FORBIDDEN, "Must have an active enrollment to review a course");
        }

        if (courseReviewRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Review already exists for this course");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EdTechException(ErrorCode.USER_NOT_FOUND, "User not found"));

        CourseReview review = new CourseReview();
        review.setCourse(course);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CourseReview savedReview = courseReviewRepository.save(review);
        updateCourseAggregateRating(course, request.getRating(), true);

        return mapToResponse(savedReview);
    }

    @Transactional
    public CourseReviewResponse updateReview(UUID userId, UUID courseId, UUID reviewId, CourseReviewRequest request) {
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new EdTechException(ErrorCode.FORBIDDEN, "You can only update your own review");
        }

        if (!review.getCourse().getId().equals(courseId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Review does not belong to this course");
        }

        Integer oldRating = review.getRating();
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        CourseReview savedReview = courseReviewRepository.save(review);

        if (!oldRating.equals(request.getRating())) {
            updateCourseAggregateRatingForUpdate(review.getCourse(), oldRating, request.getRating());
        }

        return mapToResponse(savedReview);
    }

    @Transactional
    public void deleteReview(UUID userId, UUID courseId, UUID reviewId) {
        CourseReview review = courseReviewRepository.findById(reviewId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Review not found"));

        if (!review.getUser().getId().equals(userId)) {
            throw new EdTechException(ErrorCode.FORBIDDEN, "You can only delete your own review");
        }

        if (!review.getCourse().getId().equals(courseId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Review does not belong to this course");
        }

        Integer rating = review.getRating();
        Course course = review.getCourse();
        
        courseReviewRepository.delete(review);
        updateCourseAggregateRating(course, rating, false);
    }

    @Transactional(readOnly = true)
    public Page<CourseReviewResponse> getCourseReviews(UUID courseId, Pageable pageable) {
        if (!courseRepository.existsById(courseId)) {
            throw new EdTechException(ErrorCode.COURSE_NOT_FOUND, "Course not found");
        }
        return courseReviewRepository.findByCourseIdWithUser(courseId, pageable).map(this::mapToResponse);
    }

    private void updateCourseAggregateRating(Course course, int rating, boolean isAddition) {
        int oldCount = course.getReviewCount() != null ? course.getReviewCount() : 0;
        BigDecimal oldRating = course.getRating() != null ? course.getRating() : BigDecimal.ZERO;
        
        double oldTotal = oldRating.doubleValue() * oldCount;
        
        int newCount = isAddition ? oldCount + 1 : oldCount - 1;
        
        if (newCount <= 0) {
            course.setReviewCount(0);
            course.setRating(null);
        } else {
            double newTotal = isAddition ? (oldTotal + rating) : (oldTotal - rating);
            double newAverage = newTotal / newCount;
            
            course.setReviewCount(newCount);
            course.setRating(BigDecimal.valueOf(newAverage).setScale(1, RoundingMode.HALF_UP));
        }
        
        courseRepository.save(course);
    }

    private void updateCourseAggregateRatingForUpdate(Course course, int oldIndividualRating, int newIndividualRating) {
        int count = course.getReviewCount() != null ? course.getReviewCount() : 0;
        if (count == 0) return; // Should not happen
        
        BigDecimal oldAvg = course.getRating() != null ? course.getRating() : BigDecimal.ZERO;
        double oldTotal = oldAvg.doubleValue() * count;
        
        double newTotal = oldTotal - oldIndividualRating + newIndividualRating;
        double newAverage = newTotal / count;
        
        course.setRating(BigDecimal.valueOf(newAverage).setScale(1, RoundingMode.HALF_UP));
        courseRepository.save(course);
    }

    private CourseReviewResponse mapToResponse(CourseReview review) {
        return CourseReviewResponse.builder()
                .id(review.getId())
                .courseId(review.getCourse().getId())
                .userId(review.getUser().getId())
                .studentDisplayName(review.getUser().getName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
