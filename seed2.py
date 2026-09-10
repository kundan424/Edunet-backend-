import psycopg2
import uuid
import datetime

conn = psycopg2.connect("dbname='edtech_db' user='edtech_user' password='edtech_pass' host='localhost' port='5432'")
cur = conn.cursor()

now = datetime.datetime.now()

# Get a student
cur.execute("SELECT id FROM users WHERE email = 'student.alice@edunest.local'")
student_id = cur.fetchone()[0]

# Get a course
cur.execute("SELECT id FROM courses WHERE title = 'Java Backend Development'")
course_id = cur.fetchone()[0]

# Get lessons
cur.execute("SELECT id, lesson_type FROM lessons WHERE section_id IN (SELECT id FROM sections WHERE course_id = %s) ORDER BY display_order", (course_id,))
lessons = cur.fetchall()

if len(lessons) >= 2:
    l1_id, t1 = lessons[0]
    l2_id, t2 = lessons[1]
    
    # Progress
    cur.execute("INSERT INTO lesson_progress (id, user_id, course_id, lesson_id, status, max_position_seconds, last_position_seconds, created_at, updated_at) VALUES (%s, %s, %s, %s, 'COMPLETED', 300, 300, %s, %s)", (str(uuid.uuid4()), student_id, course_id, l1_id, now, now))
    cur.execute("INSERT INTO lesson_progress (id, user_id, course_id, lesson_id, status, max_position_seconds, last_position_seconds, created_at, updated_at) VALUES (%s, %s, %s, %s, 'IN_PROGRESS', 600, 400, %s, %s)", (str(uuid.uuid4()), student_id, course_id, l2_id, now, now))

# Get quiz
cur.execute("SELECT id FROM quizzes LIMIT 1")
quiz_row = cur.fetchone()
if quiz_row:
    quiz_id = quiz_row[0]
    # Quiz attempt
    cur.execute("INSERT INTO quiz_attempts (id, user_id, quiz_id, attempt_number, status, score, percentage, passed, started_at, completed_at) VALUES (%s, %s, %s, 1, 'SUBMITTED', 100, 100.0, true, %s, %s)", (str(uuid.uuid4()), student_id, quiz_id, now, now))

# Get assignment
cur.execute("SELECT id FROM assignments LIMIT 1")
assign_row = cur.fetchone()
if assign_row:
    assign_id = assign_row[0]
    # Assignment submission
    cur.execute("INSERT INTO assignment_submissions (id, user_id, assignment_id, submission_text, score, feedback, status) VALUES (%s, %s, %s, 'Here is my work', 95, 'Good job', 'GRADED')", (str(uuid.uuid4()), student_id, assign_id))

conn.commit()
cur.close()
conn.close()
print("Seeded progress")
