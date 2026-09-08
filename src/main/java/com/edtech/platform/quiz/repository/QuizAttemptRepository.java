package com.edtech.platform.quiz.repository;

import com.edtech.platform.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByQuizIdAndUserIdOrderByAttemptNumberAsc(UUID quizId, UUID userId);
    int countByQuizIdAndUserId(UUID quizId, UUID userId);
    Optional<QuizAttempt> findByIdAndUserId(UUID attemptId, UUID userId);
}
