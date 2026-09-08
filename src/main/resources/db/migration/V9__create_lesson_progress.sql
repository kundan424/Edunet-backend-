-- Add duration_seconds to lessons
ALTER TABLE lessons ADD COLUMN duration_seconds INTEGER;

-- Create lesson_progress table
CREATE TABLE lesson_progress (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    lesson_id UUID NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    last_position_seconds INTEGER NOT NULL DEFAULT 0,
    max_position_seconds INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    
    CONSTRAINT uk_lesson_progress_user_lesson UNIQUE (user_id, lesson_id)
);

CREATE INDEX idx_lesson_progress_user_course ON lesson_progress(user_id, course_id);
