import requests
import json
import psycopg2
import time
import subprocess
import os

BASE_URL = 'http://localhost:8080/api/v1'

# Users to create
ADMINS = ['admin@edunest.local', 'admin2@edunest.local']
INSTRUCTORS = ['instructor.java@edunest.local', 'instructor.spring@edunest.local', 'instructor.cloud@edunest.local', 'instructor.database@edunest.local']
STUDENTS = ['student.alice@edunest.local', 'student.bob@edunest.local', 'student.charlie@edunest.local', 'student.david@edunest.local', 'student.emma@edunest.local', 'student.frank@edunest.local']

PASSWORD = 'Admin@12345!'

# Store tokens and ids
tokens = {}
user_ids = {}

def register_and_login(email, name, role):
    req = {'name': name, 'email': email, 'password': PASSWORD, 'role': role}
    res = requests.post(f'{BASE_URL}/auth/register', json=req)
    if res.status_code != 201:
        print(f"Failed to register {email}: {res.text}")
        return None
    data = res.json()['data']
    user_ids[email] = data['id']
    
    login_req = {'email': email, 'password': PASSWORD}
    login_res = requests.post(f'{BASE_URL}/auth/login', json=login_req)
    if login_res.status_code != 200:
        print(f"Failed to login {email}")
        return None
    token = login_res.json()['data']['token']
    tokens[email] = token
    return token

def make_admin(email):
    # Update DB
    conn = psycopg2.connect("dbname='edtech_db' user='edtech_user' password='edtech_pass' host='localhost' port='5432'")
    cur = conn.cursor()
    cur.execute("UPDATE users SET role = 'ADMIN' WHERE email = %s", (email,))
    conn.commit()
    cur.close()
    conn.close()
    # Re-login to get admin token
    return register_and_login(email, email.split('@')[0], 'ADMIN') # it'll fail register, we need separate login

def login(email):
    login_req = {'email': email, 'password': PASSWORD}
    login_res = requests.post(f'{BASE_URL}/auth/login', json=login_req)
    if login_res.status_code == 200:
        tokens[email] = login_res.json()['data']['token']
        return tokens[email]
    return None

print("Registering users...")
for e in ADMINS: register_and_login(e, e.split('@')[0], 'STUDENT') # register as student first
for e in INSTRUCTORS: register_and_login(e, e.split('@')[0], 'INSTRUCTOR')
for e in STUDENTS: register_and_login(e, e.split('@')[0], 'STUDENT')

print("Promoting admins...")
for e in ADMINS:
    conn = psycopg2.connect("dbname='edtech_db' user='edtech_user' password='edtech_pass' host='localhost' port='5432'")
    cur = conn.cursor()
    cur.execute("UPDATE users SET role = 'ADMIN' WHERE email = %s", (e,))
    conn.commit()
    cur.close()
    conn.close()
    login(e)

print("Creating instructor profiles...")
def create_profile(email, bio, expertise):
    t = tokens[email]
    req = {'bio': bio, 'expertise': expertise}
    res = requests.post(f'{BASE_URL}/instructors/profile', json=req, headers={'Authorization': f'Bearer {t}'})
    if res.status_code != 200: print(f"Failed profile {email}: {res.text}")
    
    res = requests.post(f'{BASE_URL}/instructors/verification', headers={'Authorization': f'Bearer {t}'})
    if res.status_code != 200: print(f"Failed verification request {email}: {res.text}")

create_profile('instructor.java@edunest.local', 'Backend engineer specializing in Java and JVM development.', 'Java, Spring Boot, REST APIs')
create_profile('instructor.spring@edunest.local', 'Spring ecosystem instructor focused on production backend systems.', 'Spring Boot, Spring Security, JPA')
create_profile('instructor.cloud@edunest.local', 'Cloud architecture and deployment specialist.', 'AWS, Docker, Cloud Architecture')
create_profile('instructor.database@edunest.local', 'Database engineer focused on relational systems.', 'PostgreSQL, SQL, Database Design')

print("Verifying instructors...")
admin_t = tokens[ADMINS[0]]
# Get pending
res = requests.get(f'{BASE_URL}/admin/instructors/pending', headers={'Authorization': f'Bearer {admin_t}'})
if res.status_code == 200:
    pending = res.json()['data']
    for p in pending:
        # Don't verify database instructor
        email = [k for k,v in user_ids.items() if v == p['userId']][0]
        if email != 'instructor.database@edunest.local':
            requests.post(f'{BASE_URL}/admin/instructors/{p["id"]}/verify', headers={'Authorization': f'Bearer {admin_t}'})

print("Creating courses...")
courses = []

def create_course(email, req):
    t = tokens[email]
    res = requests.post(f'{BASE_URL}/instructors/courses', json=req, headers={'Authorization': f'Bearer {t}'})
    if res.status_code == 201:
        c = res.json()['data']
        courses.append(c)
        return c
    else:
        print(f"Failed course {req['title']}: {res.text}")
        return None

c1 = create_course('instructor.java@edunest.local', {'title': 'Java Backend Development', 'category': 'Development', 'difficulty': 'BEGINNER', 'price': 0, 'description': 'Learn Java'})
c2 = create_course('instructor.spring@edunest.local', {'title': 'Mastering Spring Boot', 'category': 'Development', 'difficulty': 'INTERMEDIATE', 'price': 49.99, 'description': 'Learn Spring Boot'})
c3 = create_course('instructor.spring@edunest.local', {'title': 'Advanced Spring Security', 'category': 'Development', 'difficulty': 'ADVANCED', 'price': 79.99, 'description': 'Learn Spring Security'})
c4 = create_course('instructor.database@edunest.local', {'title': 'PostgreSQL Fundamentals', 'category': 'Database', 'difficulty': 'BEGINNER', 'price': 0, 'description': 'Learn Postgres'})
c5 = create_course('instructor.database@edunest.local', {'title': 'Advanced PostgreSQL Performance', 'category': 'Database', 'difficulty': 'ADVANCED', 'price': 59.99, 'description': 'Learn Postgres Adv'})
c6 = create_course('instructor.cloud@edunest.local', {'title': 'AWS Cloud Architecture', 'category': 'Cloud', 'difficulty': 'ADVANCED', 'price': 89.99, 'description': 'Learn AWS'})
c7 = create_course('instructor.spring@edunest.local', {'title': 'Spring Boot Microservices', 'category': 'Architecture', 'difficulty': 'ADVANCED', 'price': 99.99, 'description': 'Learn Microservices'})
c8 = create_course('instructor.cloud@edunest.local', {'title': 'Docker for Java Developers', 'category': 'DevOps', 'difficulty': 'INTERMEDIATE', 'price': 29.99, 'description': 'Learn Docker'})

print("Creating curriculum...")
def create_section(email, course_id, req):
    res = requests.post(f'{BASE_URL}/instructors/courses/{course_id}/sections', json=req, headers={'Authorization': f'Bearer {tokens[email]}'})
    return res.json()['data'] if res.status_code == 201 else None

def create_lesson(email, course_id, section_id, req):
    res = requests.post(f'{BASE_URL}/instructors/courses/{course_id}/sections/{section_id}/lessons', json=req, headers={'Authorization': f'Bearer {tokens[email]}'})
    return res.json()['data'] if res.status_code == 201 else None

for c in [c1, c2, c3, c4, c5, c6, c7, c8]:
    if not c: continue
    email = [k for k,v in user_ids.items() if v == c['instructorId']][0]
    
    s1 = create_section(email, c['id'], {'title': 'Section 1', 'displayOrder': 1})
    l1 = create_lesson(email, c['id'], s1['id'], {'title': 'Intro', 'lessonType': 'VIDEO', 'displayOrder': 1, 'durationSeconds': 300})
    l2 = create_lesson(email, c['id'], s1['id'], {'title': 'Text', 'lessonType': 'TEXT', 'displayOrder': 2})
    
    s2 = create_section(email, c['id'], {'title': 'Section 2', 'displayOrder': 2})
    l3 = create_lesson(email, c['id'], s2['id'], {'title': 'Adv', 'lessonType': 'VIDEO', 'displayOrder': 1, 'durationSeconds': 600})
    
    # Quiz for all
    lq = create_lesson(email, c['id'], s2['id'], {'title': 'Quiz', 'lessonType': 'QUIZ', 'displayOrder': 2})
    q_req = {'title': 'Midterm', 'passScore': 50, 'attemptsAllowed': 3, 'timeLimitSeconds': 1800}
    res_q = requests.post(f'{BASE_URL}/instructors/courses/{c["id"]}/lessons/{lq["id"]}/quiz', json=q_req, headers={'Authorization': f'Bearer {tokens[email]}'})
    if res_q.status_code == 201:
        # add question
        qq_req = {
            'questionText': 'Is this true?', 'questionType': 'TRUE_FALSE', 'points': 100, 'displayOrder': 1,
            'options': [{'optionText': 'True', 'displayOrder': 1, 'isCorrect': True}, {'optionText': 'False', 'displayOrder': 2, 'isCorrect': False}]
        }
        requests.post(f'{BASE_URL}/instructors/courses/{c["id"]}/lessons/{lq["id"]}/quiz/questions', json=qq_req, headers={'Authorization': f'Bearer {tokens[email]}'})

    # Assignment for all
    la = create_lesson(email, c['id'], s2['id'], {'title': 'Assignment', 'lessonType': 'ASSIGNMENT', 'displayOrder': 3})
    a_req = {'title': 'Final Project', 'instructions': 'Do it', 'maxScore': 100}
    requests.post(f'{BASE_URL}/instructors/courses/{c["id"]}/lessons/{la["id"]}/assignment', json=a_req, headers={'Authorization': f'Bearer {tokens[email]}'})

print("Publishing courses...")
# Wait database instructor courses can't be published because unverified
def publish(email, course_id):
    requests.post(f'{BASE_URL}/instructors/courses/{course_id}/submit', headers={'Authorization': f'Bearer {tokens[email]}'})
    
publish('instructor.java@edunest.local', c1['id'])
publish('instructor.spring@edunest.local', c2['id'])
publish('instructor.spring@edunest.local', c3['id'])
publish('instructor.database@edunest.local', c4['id']) # will fail since not verified, which is correct
publish('instructor.database@edunest.local', c5['id']) # will fail
publish('instructor.cloud@edunest.local', c6['id'])
# c7 draft
publish('instructor.cloud@edunest.local', c8['id']) # pending approval

# Set status directly in DB since there's no admin publish endpoint? Wait, course gets published?
# Actually submitForApproval sets PENDING_APPROVAL. We need SQL to set PUBLISHED since the admin endpoint might not exist or we didn't check it.
conn = psycopg2.connect("dbname='edtech_db' user='edtech_user' password='edtech_pass' host='localhost' port='5432'")
cur = conn.cursor()
for cid in [c1['id'], c2['id'], c3['id'], c6['id']]:
    cur.execute("UPDATE courses SET publish_status = 'PUBLISHED' WHERE id = %s", (cid,))
conn.commit()
cur.close()
conn.close()

print("Enrolling free...")
res = requests.post(f'{BASE_URL}/courses/{c1["id"]}/enroll', headers={'Authorization': f'Bearer {tokens[STUDENTS[0]]}'})
print(res.status_code)
requests.post(f'{BASE_URL}/courses/{c1["id"]}/enroll', headers={'Authorization': f'Bearer {tokens[STUDENTS[1]]}'})

print("Enrolling paid via Stripe CLI...")
res = requests.post(f'{BASE_URL}/courses/{c2["id"]}/checkout', headers={'Authorization': f'Bearer {tokens[STUDENTS[0]]}'})
if res.status_code == 200:
    url = res.json()['checkoutUrl']
    print(f"Checkout URL: {url}")
    # Wait, better trigger via CLI
    cs_id = url.split('/')[-1].split('#')[0]
    # In Stripe test mode, we might just need to trigger completion or manually pay.
    print(f"Run this to pay: docker exec stripe-cli stripe trigger checkout.session.completed --add checkout_session:client_reference_id={c2['id']}...")
    # Actually just trigger it directly via API or CLI.

print("Done seeding")
