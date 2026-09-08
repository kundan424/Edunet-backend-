package com.edtech.platform.quiz.controller;

import com.edtech.platform.common.response.ApiResponse;
import com.edtech.platform.common.security.UserDetailsImpl;
import com.edtech.platform.quiz.dto.student.QuizAttemptResponse;
import com.edtech.platform.quiz.dto.student.QuizSubmitRequest;
import com.edtech.platform.quiz.service.QuizTakingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/{courseId}/quizzes/{quizId}/attempts")
@RequiredArgsConstructor
public class StudentQuizController {

    private final QuizTakingService quizTakingService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> startAttempt(
            @PathVariable UUID courseId,
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        QuizAttemptResponse response = quizTakingService.startAttempt(userDetails.getId(), courseId, quizId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> getActiveAttempt(
            @PathVariable UUID courseId,
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        QuizAttemptResponse response = quizTakingService.getActiveAttempt(userDetails.getId(), courseId, quizId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> submitAttempt(
            @PathVariable UUID courseId,
            @PathVariable UUID quizId,
            @PathVariable UUID attemptId,
            @Valid @RequestBody QuizSubmitRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        QuizAttemptResponse response = quizTakingService.submitAttempt(userDetails.getId(), courseId, quizId, attemptId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuizAttemptResponse>>> getAttemptHistory(
            @PathVariable UUID courseId,
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<QuizAttemptResponse> response = quizTakingService.getAttemptHistory(userDetails.getId(), courseId, quizId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse<com.edtech.platform.quiz.dto.student.StudentQuizResponse>> getQuizDetails(
            @PathVariable UUID courseId,
            @PathVariable UUID quizId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        com.edtech.platform.quiz.dto.student.StudentQuizResponse response = quizTakingService.getQuizDetails(userDetails.getId(), courseId, quizId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
