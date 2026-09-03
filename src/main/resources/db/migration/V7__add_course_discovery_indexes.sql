CREATE INDEX idx_courses_publish_status_category ON courses(publish_status, category);
CREATE INDEX idx_courses_publish_status_difficulty ON courses(publish_status, difficulty);
CREATE INDEX idx_courses_price ON courses(price);
CREATE INDEX idx_courses_rating ON courses(rating);
