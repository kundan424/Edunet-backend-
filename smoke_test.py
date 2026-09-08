import requests
import subprocess
import json
import time

BASE_URL = "http://localhost:8080/api/v1"
DB_CMD = ["docker", "exec", "edtech-postgres", "psql", "-U", "edtech_user", "-d", "edtech_db", "-t", "-c"]

def run_db(query):
    result = subprocess.run(DB_CMD + [query], capture_output=True, text=True)
    if result.returncode != 0:
        print(f"DB Query failed: {result.stderr}")
        return None
    return result.stdout.strip()

print("--- STEP 1: Register Instructor ---")
res = requests.post(f"{BASE_URL}/auth/register", json={
    "name": "Smoke Test Instructor",
    "email": "instructor.smoke@example.com",
    "password": "Password123!",
    "role": "INSTRUCTOR"
})
assert res.status_code == 201, f"Failed: {res.text}"
instructor_id = res.json()["data"]["id"]
print(f"Instructor registered: {instructor_id}")

print("--- STEP 2: Login Instructor ---")
res = requests.post(f"{BASE_URL}/auth/login", json={
    "email": "instructor.smoke@example.com",
    "password": "Password123!"
})
assert res.status_code == 200, f"Failed: {res.text}"
instructor_jwt = res.json()["data"]["token"]
print("Instructor JWT obtained.")

print("--- STEP 3: Create Instructor Profile ---")
res = requests.post(f"{BASE_URL}/instructors/profile", 
    headers={"Authorization": f"Bearer {instructor_jwt}"},
    json={
        "bio": "Smoke test instructor for the EdTech platform",
        "expertise": "Java, Spring Boot, PostgreSQL"
    }
)
assert res.status_code in (200, 201), f"Failed: {res.text}"
profile_id = res.json()["data"]["id"]
print(f"Profile created: {profile_id}")

print("--- STEP 4: Verify Instructor Profile State ---")
res = requests.get(f"{BASE_URL}/instructors/profile", headers={"Authorization": f"Bearer {instructor_jwt}"})
assert res.status_code == 200, f"Failed: {res.text}"
prof_status = res.json()["data"]["verificationStatus"]
assert prof_status == "UNVERIFIED", f"Expected UNVERIFIED, got {prof_status}"
print("Profile is UNVERIFIED.")

print("--- STEP 5: Request Verification ---")
res = requests.post(f"{BASE_URL}/instructors/verification", headers={"Authorization": f"Bearer {instructor_jwt}"})
assert res.status_code == 200, f"Failed: {res.text}"
db_status = run_db(f"SELECT verification_status FROM instructor_profiles WHERE user_id = '{instructor_id}';")
assert db_status == "PENDING", f"Expected PENDING in DB, got {db_status}"
print("Profile is PENDING.")

print("--- Register Student (for completeness) ---")
res = requests.post(f"{BASE_URL}/auth/register", json={
    "name": "Smoke Test Student",
    "email": "student.smoke@example.com",
    "password": "Password123!",
    "role": "STUDENT"
})
assert res.status_code == 201, f"Failed: {res.text}"
student_id = res.json()["data"]["id"]
print(f"Student registered: {student_id}")

print("--- STEP 6: Register Admin ---")
res = requests.post(f"{BASE_URL}/auth/register", json={
    "name": "Smoke Test Admin",
    "email": "admin.smoke@example.com",
    "password": "Password123!",
    "role": "STUDENT"
})
assert res.status_code == 201, f"Failed: {res.text}"
admin_id = res.json()["data"]["id"]
print(f"Admin registered (as STUDENT): {admin_id}")

print("--- STEP 7: Promote Admin ---")
run_db(f"UPDATE users SET role = 'ADMIN' WHERE id = '{admin_id}';")
admin_role = run_db(f"SELECT role FROM users WHERE id = '{admin_id}';")
assert admin_role == "ADMIN", f"Expected ADMIN, got {admin_role}"
print("Admin promoted in DB.")

print("--- STEP 8: Login Admin AGAIN ---")
res = requests.post(f"{BASE_URL}/auth/login", json={
    "email": "admin.smoke@example.com",
    "password": "Password123!"
})
assert res.status_code == 200, f"Failed: {res.text}"
admin_jwt = res.json()["data"]["token"]
print("Admin JWT obtained.")

print("--- STEP 9: List Pending Instructors ---")
res = requests.get(f"{BASE_URL}/admin/instructors/pending", headers={"Authorization": f"Bearer {admin_jwt}"})
assert res.status_code == 200, f"Failed: {res.text}"
pending = res.json()["data"]
found = any(p["userId"] == instructor_id for p in pending)
assert found, "Instructor not found in pending list"
print("Instructor is in pending list.")

print("--- STEP 10: Approve Instructor ---")
res = requests.post(f"{BASE_URL}/admin/instructors/{instructor_id}/verify", 
    headers={"Authorization": f"Bearer {admin_jwt}"},
    json={"status": "VERIFIED"}
)
assert res.status_code == 200, f"Failed: {res.text}"
db_status = run_db(f"SELECT verification_status FROM instructor_profiles WHERE user_id = '{instructor_id}';")
assert db_status == "VERIFIED", f"Expected VERIFIED in DB, got {db_status}"
print("Instructor APPROVED and VERIFIED.")

print("--- STEP 11: Create Course ---")
res = requests.post(f"{BASE_URL}/instructors/courses", 
    headers={"Authorization": f"Bearer {instructor_jwt}"},
    json={
        "title": "SMOKE TEST - Spring Boot Course",
        "description": "Persistent end-to-end smoke-test course",
        "category": "Development",
        "difficulty": "INTERMEDIATE",
        "price": 0.00
    }
)
assert res.status_code in (200, 201), f"Failed: {res.text}"
course_id = res.json()["data"]["id"]
print(f"Course created: {course_id}")

print("--- STEP 12: Create Section ---")
res = requests.post(f"{BASE_URL}/instructors/courses/{course_id}/sections", 
    headers={"Authorization": f"Bearer {instructor_jwt}"},
    json={
        "title": "Smoke Test Section",
        "description": "Section created during persistent smoke test",
        "displayOrder": 1
    }
)
assert res.status_code in (200, 201), f"Failed: {res.text}"
section_id = res.json()["data"]["id"]
print(f"Section created: {section_id}")

print("--- STEP 13: Create Lesson ---")
res = requests.post(f"{BASE_URL}/instructors/courses/{course_id}/sections/{section_id}/lessons", 
    headers={"Authorization": f"Bearer {instructor_jwt}"},
    json={
        "title": "Smoke Test Lesson",
        "description": "Lesson created during persistent smoke test",
        "lessonType": "TEXT",
        "displayOrder": 1
    }
)
assert res.status_code in (200, 201), f"Failed: {res.text}"
lesson_id = res.json()["data"]["id"]
print(f"Lesson created: {lesson_id}")

print("--- STEP 14: Submit Course ---")
res = requests.post(f"{BASE_URL}/instructors/courses/{course_id}/submit", 
    headers={"Authorization": f"Bearer {instructor_jwt}"}
)
assert res.status_code == 200, f"Failed: {res.text}"
db_status = run_db(f"SELECT publish_status FROM courses WHERE id = '{course_id}';")
assert db_status == "PENDING_APPROVAL", f"Expected PENDING_APPROVAL, got {db_status}"
print("Course submitted successfully.")

print("--- Public Discovery Check ---")
res = requests.get(f"{BASE_URL}/courses")
assert res.status_code == 200
found = any(c["id"] == course_id for c in res.json()["content"])
assert not found, "Course should NOT be publicly discoverable yet"
print("Course is correctly hidden from public discovery.")

print("--- Phase 4 Public Discovery Test (Promote to PUBLISHED) ---")
run_db(f"UPDATE courses SET publish_status = 'PUBLISHED' WHERE id = '{course_id}';")
res = requests.get(f"{BASE_URL}/courses")
found = any(c["id"] == course_id for c in res.json()["content"])
assert found, "Course SHOULD be publicly discoverable now"
print("Course is now PUBLICLY DISCOVERABLE.")

print("--- SEARCH CHECK ---")
res = requests.get(f"{BASE_URL}/courses?search=SMOKE")
assert any(c["id"] == course_id for c in res.json()["content"]), "Search failed"
res = requests.get(f"{BASE_URL}/courses?difficulty=INTERMEDIATE")
assert any(c["id"] == course_id for c in res.json()["content"]), "Difficulty filter failed"
res = requests.get(f"{BASE_URL}/courses?minPrice=0&maxPrice=0")
assert any(c["id"] == course_id for c in res.json()["content"]), "Price filter failed"

print("--- Get Course By ID (Curriculum Check) ---")
res = requests.get(f"{BASE_URL}/courses/{course_id}")
assert res.status_code == 200, f"Failed: {res.text}"
data = res.json()
assert "sections" in data and len(data["sections"]) > 0, "Sections missing"
assert "lessons" in data["sections"][0] and len(data["sections"][0]["lessons"]) > 0, "Lessons missing"
print("Course curriculum fully retrieved.")

print("\n--- DB PERSISTENCE REPORT ---")
print("USERS:")
print(run_db("SELECT id, name, email, role, status FROM users WHERE email IN ('admin.smoke@example.com', 'instructor.smoke@example.com', 'student.smoke@example.com');"))
print("INSTRUCTOR PROFILE:")
print(run_db(f"SELECT id, user_id, bio, expertise, verification_status FROM instructor_profiles WHERE user_id = '{instructor_id}';"))
print("COURSE:")
print(run_db(f"SELECT id, instructor_id, title, publish_status, price FROM courses WHERE id = '{course_id}';"))
print("SECTION:")
print(run_db(f"SELECT id, course_id, title, display_order FROM sections WHERE course_id = '{course_id}';"))
print("LESSON:")
print(run_db(f"SELECT id, section_id, title, lesson_type, display_order FROM lessons WHERE section_id = '{section_id}';"))

print("\n--- DONE ---")
