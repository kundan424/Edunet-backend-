-- Enable pg_trgm extension for search
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Add review_count to courses
ALTER TABLE courses ADD COLUMN review_count INTEGER DEFAULT 0 NOT NULL;

-- Create course_reviews table
CREATE TABLE course_reviews (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (user_id, course_id)
);

-- Create notifications table
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    reference_type VARCHAR(50),
    reference_id VARCHAR(255),
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE
);

-- Create search indexes
CREATE INDEX idx_courses_title_trgm ON courses USING gin (lower(title) gin_trgm_ops);
CREATE INDEX idx_courses_desc_trgm ON courses USING gin (lower(description) gin_trgm_ops);
CREATE INDEX idx_notifications_user_id ON notifications(user_id, created_at DESC);
