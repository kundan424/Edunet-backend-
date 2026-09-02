package com.edtech.platform.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Generic
    INTERNAL_SERVER_ERROR("ERR_0001", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_FAILED("ERR_0002", "Validation failed", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("ERR_0003", "Resource not found", HttpStatus.NOT_FOUND),
    CONFLICT("ERR_0004", "Resource already exists", HttpStatus.CONFLICT),
    UNAUTHORIZED("ERR_0005", "Unauthorized access", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("ERR_0006", "Access denied", HttpStatus.FORBIDDEN),

    // Auth
    INVALID_CREDENTIALS("ERR_1001", "Invalid email or password", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("ERR_1002", "Authentication token has expired", HttpStatus.UNAUTHORIZED),
    TOKEN_INVALID("ERR_1003", "Authentication token is invalid", HttpStatus.UNAUTHORIZED),
    EMAIL_ALREADY_EXISTS("ERR_1004", "Email address is already registered", HttpStatus.CONFLICT),

    // User
    USER_NOT_FOUND("ERR_2001", "User not found", HttpStatus.NOT_FOUND),
    USER_NOT_ACTIVE("ERR_2002", "User account is not active", HttpStatus.FORBIDDEN),

    // Instructor
    INSTRUCTOR_NOT_VERIFIED("ERR_3001", "Instructor is not verified", HttpStatus.FORBIDDEN),
    INSTRUCTOR_PROFILE_NOT_FOUND("ERR_3002", "Instructor profile not found", HttpStatus.NOT_FOUND),
    INSTRUCTOR_ALREADY_VERIFIED("ERR_3003", "Instructor is already verified", HttpStatus.CONFLICT),

    // Course
    COURSE_NOT_FOUND("ERR_4001", "Course not found", HttpStatus.NOT_FOUND),
    COURSE_NOT_PUBLISHED("ERR_4002", "Course is not published", HttpStatus.FORBIDDEN),
    COURSE_ACCESS_DENIED("ERR_4003", "You do not have access to this course", HttpStatus.FORBIDDEN),
    INVALID_COURSE_STATE_TRANSITION("ERR_4004", "Invalid course state transition", HttpStatus.BAD_REQUEST),

    // Enrollment
    ALREADY_ENROLLED("ERR_5001", "You are already enrolled in this course", HttpStatus.CONFLICT),
    ENROLLMENT_NOT_FOUND("ERR_5002", "Enrollment not found", HttpStatus.NOT_FOUND),
    NOT_ENROLLED("ERR_5003", "You are not enrolled in this course", HttpStatus.FORBIDDEN),

    // Payment
    PAYMENT_NOT_FOUND("ERR_6001", "Payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_FAILED("ERR_6002", "Payment processing failed", HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_COMPLETED("ERR_6003", "Payment has already been completed", HttpStatus.CONFLICT),
    INVALID_WEBHOOK_SIGNATURE("ERR_6004", "Invalid webhook signature", HttpStatus.UNAUTHORIZED),

    // Quiz
    QUIZ_NOT_FOUND("ERR_7001", "Quiz not found", HttpStatus.NOT_FOUND),
    QUIZ_ATTEMPT_LIMIT_EXCEEDED("ERR_7002", "Quiz attempt limit has been exceeded", HttpStatus.FORBIDDEN),
    QUIZ_ATTEMPT_NOT_FOUND("ERR_7003", "Quiz attempt not found", HttpStatus.NOT_FOUND),
    INVALID_QUIZ_SUBMISSION("ERR_7004", "Invalid quiz submission", HttpStatus.BAD_REQUEST),

    // Assignment
    ASSIGNMENT_NOT_FOUND("ERR_8001", "Assignment not found", HttpStatus.NOT_FOUND),
    SUBMISSION_NOT_FOUND("ERR_8002", "Assignment submission not found", HttpStatus.NOT_FOUND),
    SUBMISSION_ALREADY_GRADED("ERR_8003", "Submission has already been graded", HttpStatus.CONFLICT),

    // Media
    MEDIA_NOT_FOUND("ERR_9001", "Media not found", HttpStatus.NOT_FOUND),
    MEDIA_NOT_READY("ERR_9002", "Media is not ready for playback", HttpStatus.ACCEPTED),
    UPLOAD_FAILED("ERR_9003", "File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}