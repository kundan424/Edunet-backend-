import requests
import json
import uuid
import os

base_url = 'http://localhost:8080/api/v1'

def run_tests():
    # Register instructor
    inst_email = f"inst_{uuid.uuid4()}@test.com"
    requests.post(f"{base_url}/auth/register", json={
        "email": inst_email, "password": "password123", "name": "Test Instructor", "role": "INSTRUCTOR"
    })
    
    # Login instructor
    resp = requests.post(f"{base_url}/auth/login", json={"email": inst_email, "password": "password123"})
    inst_token = resp.json()['data']['token']
    inst_headers = {"Authorization": f"Bearer {inst_token}"}
    
    # Get user id and insert profile manually to skip approval
    import subprocess
    cmd = f'docker exec edtech-postgres psql -U edtech_user -d edtech_db -c "INSERT INTO instructor_profiles (id, user_id, bio, expertise, verification_status, created_at, updated_at) SELECT gen_random_uuid(), id, \'Bio\', \'Expertise\', \'APPROVED\', NOW(), NOW() FROM users WHERE email = \'{inst_email}\';"'
    subprocess.run(cmd, shell=True)
    
    # Create course
    resp = requests.post(f"{base_url}/instructors/courses", json={
        "title": "Media Course", "description": "Test media upload", "price": 10.0, "status": "DRAFT"
    }, headers=inst_headers)
    course_id = resp.json()['data']['id']
    
    # Publish course
    requests.post(f"{base_url}/instructors/courses/{course_id}/publish", headers=inst_headers)

    # Create section
    resp = requests.post(f"{base_url}/instructors/courses/{course_id}/sections", json={
        "title": "S1", "displayOrder": 1
    }, headers=inst_headers)
    print("Create Section:", resp.text)
    section_id = resp.json()['data']['id']

    # Create lesson
    resp = requests.post(f"{base_url}/instructors/courses/{course_id}/sections/{section_id}/lessons", json={
        "title": "Video Lesson", "content": "Watch this", "displayOrder": 1, "lessonType": "VIDEO"
    }, headers=inst_headers)
    print("Create Lesson:", resp.text)
    lesson_id = resp.json()['data']['id']
    
    # Upload video A
    with open("videoA.mp4", "wb") as f:
        f.write(b"video content A")
    
    with open("videoA.mp4", "rb") as f:
        resp = requests.post(
            f"{base_url}/instructors/courses/{course_id}/lessons/{lesson_id}/media",
            headers=inst_headers,
            files={"file": ("videoA.mp4", f, "video/mp4")}
        )
    print("Upload A:", resp.status_code, resp.text)
    
    # Register Student A
    stu_a_email = f"stuA_{uuid.uuid4()}@test.com"
    requests.post(f"{base_url}/auth/register", json={
        "email": stu_a_email, "password": "password123", "name": "Stu A", "role": "STUDENT"
    })
    resp = requests.post(f"{base_url}/auth/login", json={"email": stu_a_email, "password": "password123"})
    stu_a_token = resp.json()['data']['token']
    stu_a_headers = {"Authorization": f"Bearer {stu_a_token}"}
    
    # Enroll Student A
    requests.post(f"{base_url}/courses/{course_id}/enroll", headers=stu_a_headers)
    
    # Get Media Student A (Play)
    resp = requests.get(f"{base_url}/courses/{course_id}/lessons/{lesson_id}/media", headers=stu_a_headers)
    print("Play A Status:", resp.status_code)
    print("Play A Content:", resp.content)
    
    # Range Test
    range_headers = {"Authorization": f"Bearer {stu_a_token}", "Range": "bytes=0-4"}
    resp = requests.get(f"{base_url}/courses/{course_id}/lessons/{lesson_id}/media", headers=range_headers)
    print("Range Play A Status:", resp.status_code)
    print("Range Play A Content:", resp.content)
    
    # Register Student B (Unenrolled)
    stu_b_email = f"stuB_{uuid.uuid4()}@test.com"
    requests.post(f"{base_url}/auth/register", json={
        "email": stu_b_email, "password": "password123", "name": "Stu B", "role": "STUDENT"
    })
    resp = requests.post(f"{base_url}/auth/login", json={"email": stu_b_email, "password": "password123"})
    stu_b_token = resp.json()['data']['token']
    stu_b_headers = {"Authorization": f"Bearer {stu_b_token}"}
    
    # Unenrolled Get Media
    resp = requests.get(f"{base_url}/courses/{course_id}/lessons/{lesson_id}/media", headers=stu_b_headers)
    print("Unenrolled Play Status:", resp.status_code)
    
    # Anonymous Play
    resp = requests.get(f"{base_url}/courses/{course_id}/lessons/{lesson_id}/media")
    print("Anonymous Play Status:", resp.status_code)
    
    # Replace Video
    with open("videoB.mp4", "wb") as f:
        f.write(b"video content B is longer")
    
    with open("videoB.mp4", "rb") as f:
        resp = requests.post(
            f"{base_url}/instructors/courses/{course_id}/lessons/{lesson_id}/media",
            headers=inst_headers,
            files={"file": ("videoB.mp4", f, "video/mp4")}
        )
    print("Upload B Status:", resp.status_code)
    
    # Register Instructor B
    inst_b_email = f"instB_{uuid.uuid4()}@test.com"
    requests.post(f"{base_url}/auth/register", json={
        "email": inst_b_email, "password": "password123", "name": "Test Instructor", "role": "INSTRUCTOR"
    })
    resp = requests.post(f"{base_url}/auth/login", json={"email": inst_b_email, "password": "password123"})
    inst_b_token = resp.json()['data']['token']
    inst_b_headers = {"Authorization": f"Bearer {inst_b_token}"}
    cmd_b = f'docker exec edtech-postgres psql -U edtech_user -d edtech_db -c "INSERT INTO instructor_profiles (id, user_id, bio, expertise, verification_status, created_at, updated_at) SELECT gen_random_uuid(), id, \'Bio\', \'Expertise\', \'APPROVED\', NOW(), NOW() FROM users WHERE email = \'{inst_b_email}\';"'
    subprocess.run(cmd_b, shell=True)
    
    # Instructor B Ownership Test
    with open("videoB.mp4", "rb") as f:
        resp = requests.post(
            f"{base_url}/instructors/courses/{course_id}/lessons/{lesson_id}/media",
            headers=inst_b_headers,
            files={"file": ("videoB.mp4", f, "video/mp4")}
        )
    print("Inst B Upload Status:", resp.status_code)
    
    # Play Video B to check replacement
    resp = requests.get(f"{base_url}/courses/{course_id}/lessons/{lesson_id}/media", headers=stu_a_headers)
    print("Play B Content:", resp.content)

if __name__ == '__main__':
    run_tests()
