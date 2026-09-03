CREATE TABLE courses (
    id UUID PRIMARY KEY,
    instructor_id UUID NOT NULL REFERENCES users(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100),
    difficulty VARCHAR(50),
    price DECIMAL(10, 2),
    thumbnail_url VARCHAR(255),
    publish_status VARCHAR(50) NOT NULL,
    rating DECIMAL(3, 2),
    student_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_courses_instructor_id ON courses(instructor_id);
