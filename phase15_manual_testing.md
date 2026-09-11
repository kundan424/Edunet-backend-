# Phase 15 Manual Testing Guide

## 1. Get User Email Preferences
* **Method:** GET
* **URL:** http://localhost:8080/api/v1/users/me/email-preferences
* **Headers:** Authorization: Bearer <ANY_USER_JWT>
* **Expected Status:** 200 OK
* **Expected Response:** {"success":true,"data":{"emailNotificationsEnabled":true}}

## 2. Update User Email Preferences (Disable)
* **Method:** PUT
* **URL:** http://localhost:8080/api/v1/users/me/email-preferences
* **Headers:** Authorization: Bearer <ANY_USER_JWT>
* **Body:**
  `json
  {
    "emailNotificationsEnabled": false
  }
  `
* **Expected Status:** 200 OK
* **Expected DB Effect:** email_notifications_enabled column in users table becomes alse.

## 3. Instructor Verification -> Email
* **Method:** POST
* **URL:** http://localhost:8080/api/v1/admin/instructors/<USER_ID>/verify
* **Headers:** Authorization: Bearer <ADMIN_JWT>
* **Expected Status:** 200 OK
* **Expected Email Behavior:** Look at console logs. A Mock Email will be printed with "Subject: Profile Verified".

## 4. Course Publication -> Email
* **Method:** POST
* **URL:** http://localhost:8080/api/v1/admin/courses/<COURSE_ID>/approve
* **Headers:** Authorization: Bearer <ADMIN_JWT>
* **Expected Status:** 200 OK
* **Expected Email Behavior:** Mock Email logged with "Subject: Course Published".

## 5. Course Enrollment -> Email
* **Method:** POST
* **URL:** http://localhost:8080/api/v1/enrollments/<COURSE_ID>
* **Headers:** Authorization: Bearer <STUDENT_JWT>
* **Expected Status:** 201 Created
* **Expected Email Behavior:** Mock Email logged with "Subject: Course Enrollment Confirmation".
