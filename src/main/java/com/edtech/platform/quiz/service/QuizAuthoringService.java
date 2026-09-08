package com.edtech.platform.quiz.service;

import com.edtech.platform.common.exception.EdTechException;
import com.edtech.platform.common.exception.ErrorCode;
import com.edtech.platform.course.entity.Lesson;
import com.edtech.platform.course.enums.LessonType;
import com.edtech.platform.course.repository.LessonRepository;
import com.edtech.platform.course.service.CourseService;
import com.edtech.platform.quiz.dto.instructor.*;
import com.edtech.platform.quiz.entity.QuestionOption;
import com.edtech.platform.quiz.entity.Quiz;
import com.edtech.platform.quiz.entity.QuizQuestion;
import com.edtech.platform.quiz.repository.QuizQuestionRepository;
import com.edtech.platform.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAuthoringService {
    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final LessonRepository lessonRepository;
    private final CourseService courseService;

    private Lesson validateAndGetLesson(UUID instructorId, UUID courseId, UUID lessonId) {
        courseService.getCourseAndVerifyOwnership(instructorId, courseId);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Lesson not found"));

        if (!lesson.getSection().getCourse().getId().equals(courseId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Lesson does not belong to the given course");
        }
        if (lesson.getLessonType() != LessonType.QUIZ) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Lesson is not of type QUIZ");
        }
        return lesson;
    }

    @Transactional
    public InstructorQuizResponse createQuiz(UUID instructorId, UUID courseId, UUID lessonId, QuizCreateRequest request) {
        Lesson lesson = validateAndGetLesson(instructorId, courseId, lessonId);
        
        if (quizRepository.existsByLessonId(lessonId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Quiz already exists for this lesson");
        }

        Quiz quiz = new Quiz();
        quiz.setLesson(lesson);
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setPassScore(request.getPassScore());
        quiz.setAttemptsAllowed(request.getAttemptsAllowed());
        quiz.setTimeLimitSeconds(request.getTimeLimitSeconds());
        
        quiz = quizRepository.save(quiz);
        return mapToInstructorQuizResponse(quiz);
    }

    @Transactional
    public InstructorQuizResponse updateQuiz(UUID instructorId, UUID courseId, UUID lessonId, QuizCreateRequest request) {
        validateAndGetLesson(instructorId, courseId, lessonId);
        
        Quiz quiz = quizRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Quiz not found"));

        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setPassScore(request.getPassScore());
        quiz.setAttemptsAllowed(request.getAttemptsAllowed());
        quiz.setTimeLimitSeconds(request.getTimeLimitSeconds());
        
        quiz = quizRepository.save(quiz);
        return mapToInstructorQuizResponse(quiz);
    }

    @Transactional(readOnly = true)
    public InstructorQuizResponse getQuiz(UUID instructorId, UUID courseId, UUID lessonId) {
        validateAndGetLesson(instructorId, courseId, lessonId);
        Quiz quiz = quizRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Quiz not found"));
        return mapToInstructorQuizResponse(quiz);
    }

    @Transactional
    public InstructorQuestionResponse addQuestion(UUID instructorId, UUID courseId, UUID lessonId, QuestionCreateRequest request) {
        validateAndGetLesson(instructorId, courseId, lessonId);
        Quiz quiz = quizRepository.findByLessonId(lessonId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Quiz not found"));

        QuizQuestion question = new QuizQuestion();
        question.setQuiz(quiz);
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setPoints(request.getPoints());
        question.setDisplayOrder(request.getDisplayOrder());

        if (request.getOptions() != null) {
            List<QuestionOption> options = request.getOptions().stream().map(opt -> {
                QuestionOption qo = new QuestionOption();
                qo.setQuestion(question);
                qo.setOptionText(opt.getOptionText());
                qo.setDisplayOrder(opt.getDisplayOrder());
                qo.setIsCorrect(opt.getIsCorrect());
                return qo;
            }).collect(Collectors.toList());
            question.setOptions(options);
        }

        QuizQuestion savedQuestion = quizQuestionRepository.save(question);
        return mapToInstructorQuestionResponse(savedQuestion);
    }

    @Transactional
    public InstructorQuestionResponse updateQuestion(UUID instructorId, UUID courseId, UUID lessonId, UUID questionId, QuestionCreateRequest request) {
        validateAndGetLesson(instructorId, courseId, lessonId);
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Question not found"));
                
        if (!question.getQuiz().getLesson().getId().equals(lessonId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Question does not belong to the given lesson");
        }

        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        question.setPoints(request.getPoints());
        question.setDisplayOrder(request.getDisplayOrder());

        question.getOptions().clear();
        if (request.getOptions() != null) {
            List<QuestionOption> options = request.getOptions().stream().map(opt -> {
                QuestionOption qo = new QuestionOption();
                qo.setQuestion(question);
                qo.setOptionText(opt.getOptionText());
                qo.setDisplayOrder(opt.getDisplayOrder());
                qo.setIsCorrect(opt.getIsCorrect());
                return qo;
            }).collect(Collectors.toList());
            question.getOptions().addAll(options);
        }

        QuizQuestion savedQuestion = quizQuestionRepository.save(question);
        return mapToInstructorQuestionResponse(savedQuestion);
    }

    @Transactional
    public void deleteQuestion(UUID instructorId, UUID courseId, UUID lessonId, UUID questionId) {
        validateAndGetLesson(instructorId, courseId, lessonId);
        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EdTechException(ErrorCode.RESOURCE_NOT_FOUND, "Question not found"));
                
        if (!question.getQuiz().getLesson().getId().equals(lessonId)) {
            throw new EdTechException(ErrorCode.VALIDATION_FAILED, "Question does not belong to the given lesson");
        }

        quizQuestionRepository.delete(question);
    }

    private InstructorQuizResponse mapToInstructorQuizResponse(Quiz quiz) {
        return InstructorQuizResponse.builder()
                .id(quiz.getId())
                .lessonId(quiz.getLesson().getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passScore(quiz.getPassScore())
                .attemptsAllowed(quiz.getAttemptsAllowed())
                .timeLimitSeconds(quiz.getTimeLimitSeconds())
                .questions(quiz.getQuestions() != null ? quiz.getQuestions().stream()
                        .map(this::mapToInstructorQuestionResponse)
                        .collect(Collectors.toList()) : new ArrayList<>())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    private InstructorQuestionResponse mapToInstructorQuestionResponse(QuizQuestion question) {
        return InstructorQuestionResponse.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getQuestionType())
                .points(question.getPoints())
                .displayOrder(question.getDisplayOrder())
                .options(question.getOptions() != null ? question.getOptions().stream()
                        .map(opt -> InstructorOptionResponse.builder()
                                .id(opt.getId())
                                .optionText(opt.getOptionText())
                                .displayOrder(opt.getDisplayOrder())
                                .isCorrect(opt.getIsCorrect())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
