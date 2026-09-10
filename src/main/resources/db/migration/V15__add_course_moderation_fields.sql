ALTER TABLE courses
ADD COLUMN reviewed_by UUID,
ADD COLUMN reviewed_at TIMESTAMP,
ADD COLUMN rejection_reason VARCHAR(1000);
