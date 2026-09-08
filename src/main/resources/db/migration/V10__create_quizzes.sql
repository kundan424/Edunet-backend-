CREATE TABLE quizzes (
    id UUID PRIMARY KEY,
    lesson_id UUID NOT NULL UNIQUE REFERENCES lessons(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    pass_score INTEGER NOT NULL,
    attempts_allowed INTEGER NOT NULL,
    time_limit_seconds INTEGER,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE quiz_questions (
    id UUID PRIMARY KEY,
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    points INTEGER NOT NULL DEFAULT 1,
    display_order INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (quiz_id, display_order)
);

CREATE TABLE question_options (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    display_order INTEGER NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (question_id, display_order)
);

CREATE TABLE quiz_attempts (
    id UUID PRIMARY KEY,
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    attempt_number INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    score INTEGER,
    percentage DOUBLE PRECISION,
    passed BOOLEAN,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    UNIQUE (quiz_id, user_id, attempt_number)
);

CREATE TABLE quiz_attempt_answers (
    id UUID PRIMARY KEY,
    attempt_id UUID NOT NULL REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    points_awarded INTEGER,
    created_at TIMESTAMP NOT NULL,
    UNIQUE (attempt_id, question_id)
);

CREATE TABLE quiz_attempt_answer_options (
    answer_id UUID NOT NULL REFERENCES quiz_attempt_answers(id) ON DELETE CASCADE,
    option_id UUID NOT NULL REFERENCES question_options(id) ON DELETE CASCADE,
    PRIMARY KEY (answer_id, option_id)
);
