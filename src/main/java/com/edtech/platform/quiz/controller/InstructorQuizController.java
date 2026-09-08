package com.edtech.platform.quiz.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.quiz.dto.instructor.InstructorQuestionResponse;
import com.edtech.platform.quiz.dto.instructor.InstructorQuizResponse;
import com.edtech.platform.quiz.dto.instructor.QuestionCreateRequest;
import com.edtech.platform.quiz.dto.instructor.QuizCreateRequest;
import com.edtech.platform.quiz.service.QuizAuthoringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructors/courses/{courseId}/lessons/{lessonId}/quiz")
@RequiredArgsConstructor
public class InstructorQuizController {

    private final QuizAuthoringService quizAuthoringService;

    @GetMapping
    public ResponseEntity<ApiResponse<InstructorQuizResponse>> getQuiz(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId) {
        InstructorQuizResponse response = quizAuthoringService.getQuiz(userDetails.getId(), courseId, lessonId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InstructorQuizResponse>> createQuiz(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody QuizCreateRequest request) {
        InstructorQuizResponse response = quizAuthoringService.createQuiz(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<InstructorQuizResponse>> updateQuiz(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody QuizCreateRequest request) {
        InstructorQuizResponse response = quizAuthoringService.updateQuiz(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<InstructorQuestionResponse>> addQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @Valid @RequestBody QuestionCreateRequest request) {
        InstructorQuestionResponse response = quizAuthoringService.addQuestion(userDetails.getId(), courseId, lessonId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<InstructorQuestionResponse>> updateQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @PathVariable UUID questionId,
            @Valid @RequestBody QuestionCreateRequest request) {
        InstructorQuestionResponse response = quizAuthoringService.updateQuestion(userDetails.getId(), courseId, lessonId, questionId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId,
            @PathVariable UUID lessonId,
            @PathVariable UUID questionId) {
        quizAuthoringService.deleteQuestion(userDetails.getId(), courseId, lessonId, questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success(null));
    }
}
