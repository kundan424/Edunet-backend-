package com.edtech.platform.quiz.service;

import com.edtech.platform.common.exception.ConflictException;
import com.edtech.platform.common.exception.ForbiddenException;
import com.edtech.platform.common.exception.ResourceNotFoundException;
import com.edtech.platform.enrollment.repository.EnrollmentRepository;
import com.edtech.platform.progress.service.ProgressService;
import com.edtech.platform.quiz.dto.student.QuizAttemptResponse;
import com.edtech.platform.quiz.dto.student.QuizSubmitRequest;
import com.edtech.platform.quiz.entity.QuestionOption;
import com.edtech.platform.quiz.entity.Quiz;
import com.edtech.platform.quiz.entity.QuizAttempt;
import com.edtech.platform.quiz.entity.QuizAttemptAnswer;
import com.edtech.platform.quiz.entity.QuizQuestion;
import com.edtech.platform.quiz.enums.AttemptStatus;
import com.edtech.platform.quiz.enums.QuestionType;
import com.edtech.platform.quiz.repository.QuizAttemptRepository;
import com.edtech.platform.quiz.repository.QuizRepository;
import com.edtech.platform.user.entity.User;
import com.edtech.platform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizTakingService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ProgressService progressService;

    private void validateEnrollment(UUID userId, UUID courseId) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ForbiddenException("User is not enrolled in this course");
        }
    }

    @Transactional
    public QuizAttemptResponse startAttempt(UUID userId, UUID courseId, UUID quizId) {
        validateEnrollment(userId, courseId);
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
                
        // Check if there is an active attempt
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdAndUserIdOrderByAttemptNumberAsc(quizId, userId);
        
        for (QuizAttempt attempt : attempts) {
            if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
                return mapToResponse(attempt);
            }
        }
        
        // Check attempt limit
        if (attempts.size() >= quiz.getAttemptsAllowed()) {
            throw new ConflictException("Maximum number of attempts reached");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        QuizAttempt newAttempt = new QuizAttempt();
        newAttempt.setQuiz(quiz);
        newAttempt.setUser(user);
        newAttempt.setAttemptNumber(attempts.size() + 1);
        newAttempt.setStatus(AttemptStatus.IN_PROGRESS);
        
        newAttempt = quizAttemptRepository.save(newAttempt);
        
        return mapToResponse(newAttempt);
    }

    @Transactional(readOnly = true)
    public QuizAttemptResponse getActiveAttempt(UUID userId, UUID courseId, UUID quizId) {
        validateEnrollment(userId, courseId);
        
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdAndUserIdOrderByAttemptNumberAsc(quizId, userId);
        
        for (QuizAttempt attempt : attempts) {
            if (attempt.getStatus() == AttemptStatus.IN_PROGRESS) {
                return mapToResponse(attempt);
            }
        }
        
        throw new ResourceNotFoundException("No active attempt found");
    }

    @Transactional
    public QuizAttemptResponse submitAttempt(UUID userId, UUID courseId, UUID quizId, UUID attemptId, QuizSubmitRequest request) {
        validateEnrollment(userId, courseId);
        
        QuizAttempt attempt = quizAttemptRepository.findByIdAndUserId(attemptId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found"));
                
        if (!attempt.getQuiz().getId().equals(quizId)) {
            throw new ConflictException("Attempt does not belong to the specified quiz");
        }
        
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new ConflictException("Attempt is already completed or abandoned");
        }
        
        Quiz quiz = attempt.getQuiz();
        
        if (quiz.getTimeLimitSeconds() != null && quiz.getTimeLimitSeconds() > 0) {
            java.time.LocalDateTime expiration = attempt.getStartedAt().plusSeconds(quiz.getTimeLimitSeconds()).plusSeconds(30);
            if (java.time.LocalDateTime.now().isAfter(expiration)) {
                throw new com.edtech.platform.common.exception.EdTechException(
                        com.edtech.platform.common.exception.ErrorCode.VALIDATION_FAILED, "Attempt time limit has expired");
            }
        }
        
        int totalScore = 0;
        int maxPossibleScore = quiz.getQuestions().stream().mapToInt(QuizQuestion::getPoints).sum();
        
        Map<UUID, QuizQuestion> questionMap = quiz.getQuestions().stream()
                .collect(Collectors.toMap(QuizQuestion::getId, q -> q));
                
        List<QuizAttemptAnswer> answers = new ArrayList<>();
        
        for (QuizSubmitRequest.AnswerSubmission submission : request.getAnswers()) {
            QuizQuestion question = questionMap.get(submission.getQuestionId());
            if (question == null) {
                throw new com.edtech.platform.common.exception.EdTechException(
                        com.edtech.platform.common.exception.ErrorCode.VALIDATION_FAILED, "Invalid question ID: " + submission.getQuestionId());
            }
            
            QuizAttemptAnswer answer = new QuizAttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(question);
            
            List<QuestionOption> selectedOptions = question.getOptions().stream()
                    .filter(o -> submission.getSelectedOptionIds().contains(o.getId()))
                    .collect(Collectors.toList());
                    
            if (selectedOptions.size() != submission.getSelectedOptionIds().size()) {
                throw new com.edtech.platform.common.exception.EdTechException(
                        com.edtech.platform.common.exception.ErrorCode.VALIDATION_FAILED, "Invalid option ID provided for question: " + question.getId());
            }
                    
            answer.setSelectedOptions(selectedOptions);
            
            int points = calculatePoints(question, selectedOptions);
            answer.setPointsAwarded(points);
            totalScore += points;
            
            answers.add(answer);
        }
        
        attempt.getAnswers().addAll(answers);
        attempt.setStatus(AttemptStatus.COMPLETED);
        attempt.setCompletedAt(LocalDateTime.now());
        attempt.setScore(totalScore);
        
        double percentage = maxPossibleScore > 0 ? ((double) totalScore / maxPossibleScore) * 100 : 0.0;
        attempt.setPercentage(percentage);
        
        boolean passed = percentage >= quiz.getPassScore();
        attempt.setPassed(passed);
        
        attempt = quizAttemptRepository.save(attempt);
        
        // Update progress via ProgressService
        progressService.completeLesson(userId, courseId, quiz.getLesson().getId());
        
        return mapToResponse(attempt);
    }
    
    private int calculatePoints(QuizQuestion question, List<QuestionOption> selectedOptions) {
        Set<UUID> selectedIds = selectedOptions.stream().map(QuestionOption::getId).collect(Collectors.toSet());
        List<QuestionOption> correctOptions = question.getOptions().stream()
                .filter(QuestionOption::getIsCorrect)
                .collect(Collectors.toList());
        Set<UUID> correctIds = correctOptions.stream().map(QuestionOption::getId).collect(Collectors.toSet());
        
        if (question.getQuestionType() == QuestionType.MULTI_SELECT) {
            if (selectedIds.size() == correctIds.size() && selectedIds.containsAll(correctIds)) {
                return question.getPoints();
            }
            return 0;
        } else {
            // MCQ_SINGLE or TRUE_FALSE
            if (selectedIds.size() == 1 && correctIds.containsAll(selectedIds)) {
                return question.getPoints();
            }
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptResponse> getAttemptHistory(UUID userId, UUID courseId, UUID quizId) {
        validateEnrollment(userId, courseId);
        
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdAndUserIdOrderByAttemptNumberAsc(quizId, userId);
        return attempts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private QuizAttemptResponse mapToResponse(QuizAttempt attempt) {
        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .attemptNumber(attempt.getAttemptNumber())
                .status(attempt.getStatus())
                .score(attempt.getScore())
                .percentage(attempt.getPercentage())
                .passed(attempt.getPassed())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getCompletedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public com.edtech.platform.quiz.dto.student.StudentQuizResponse getQuizDetails(UUID userId, UUID courseId, UUID quizId) {
        validateEnrollment(userId, courseId);
        
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
                
        List<com.edtech.platform.quiz.dto.student.StudentQuestionResponse> questionResponses = quiz.getQuestions().stream()
                .map(q -> {
                    List<com.edtech.platform.quiz.dto.student.StudentOptionResponse> optionResponses = q.getOptions().stream()
                            .map(o -> com.edtech.platform.quiz.dto.student.StudentOptionResponse.builder()
                                    .id(o.getId())
                                    .optionText(o.getOptionText())
                                    .displayOrder(o.getDisplayOrder())
                                    .build())
                            .collect(Collectors.toList());
                            
                    return com.edtech.platform.quiz.dto.student.StudentQuestionResponse.builder()
                            .id(q.getId())
                            .questionText(q.getQuestionText())
                            .questionType(q.getQuestionType())
                            .points(q.getPoints())
                            .displayOrder(q.getDisplayOrder())
                            .options(optionResponses)
                            .build();
                })
                .collect(Collectors.toList());
                
        return com.edtech.platform.quiz.dto.student.StudentQuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passScore(quiz.getPassScore())
                .attemptsAllowed(quiz.getAttemptsAllowed())
                .timeLimitSeconds(quiz.getTimeLimitSeconds())
                .questions(questionResponses)
                .build();
    }
}
