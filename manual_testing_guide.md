# Phase 13 Manual Testing Guide: Dashboards & Analytics

## 1. Student Dashboard

**Objective:** Verify student dashboard correctly aggregates enrolled courses, progress, and quizzes.
**Method:** GET
**URL:** http://localhost:8080/api/v1/dashboard/student
**Headers:**
Authorization: Bearer <STUDENT_JWT>

**Expected Status:** 200 OK
**Expected Response:**
`json
{
  "success": true,
  "data": {
    "totalEnrolledCourses": 1,
    "activeCourses": 1,
    "completedCourses": 0,
    "overallCourseCompletionPercentage": 0.0,
    "unreadNotificationCount": 0,
    "continueLearning": [...]
  }
}
`

## 2. Instructor Dashboard

**Objective:** Verify instructor sees metrics for courses they own, including successful revenue.
**Method:** GET
**URL:** http://localhost:8080/api/v1/dashboard/instructor?from=2026-01-01&to=2026-12-31
**Headers:**
Authorization: Bearer <INSTRUCTOR_JWT>

**Expected Status:** 200 OK
**Expected Response:**
`json
{
  "success": true,
  "data": {
    "totalCoursesOwned": 1,
    "totalEnrolledStudents": 1,
    "totalRevenue": 100.00,
    "currency": "USD",
    "recentCourses": [...]
  }
}
`

## 3. Instructor Course Analytics

**Objective:** Verify instructor can view detailed analytics for a single owned course.
**Method:** GET
**URL:** http://localhost:8080/api/v1/dashboard/instructor/courses/<COURSE_ID>
**Headers:**
Authorization: Bearer <INSTRUCTOR_JWT>

**Expected Status:** 200 OK
**Expected Response:**
`json
{
  "success": true,
  "data": {
    "course": { ... },
    "enrollmentCount": 1,
    "successfulRevenue": 100.00
  }
}
`

## 4. Admin Dashboard

**Objective:** Verify admin sees platform-wide metrics.
**Method:** GET
**URL:** http://localhost:8080/api/v1/dashboard/admin
**Headers:**
Authorization: Bearer <ADMIN_JWT>

**Expected Status:** 200 OK
**Expected Response:**
`json
{
  "success": true,
  "data": {
    "users": { "total": 5, ... },
    "payments": { "totalSuccessful": 1, "totalAmount": 100.00 }
  }
}
`

## 5. Security Isolation

**Objective:** Ensure a student cannot access the admin dashboard.
**Method:** GET
**URL:** http://localhost:8080/api/v1/dashboard/admin
**Headers:**
Authorization: Bearer <STUDENT_JWT>

**Expected Status:** 403 Forbidden
