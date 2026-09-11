# Phase 14 Manual Testing Guide

## 1. Deep Curriculum Response
**Objective**: Fetch the entire editable curriculum (Course -> Sections -> Lessons) avoiding N+1 requests.
*   **Method**: GET
*   **URL**: http://localhost:8080/api/v1/instructors/courses/<COURSE_ID>/curriculum
*   **Headers**: Authorization: Bearer <INSTRUCTOR_JWT>
*   **Expected Status**: 200 OK
*   **Expected Response**: A JSON payload containing "course", "sections", and nested "lessons" arrays.

## 2. Safe Editing downgrades PUBLISHED to PENDING_APPROVAL
**Objective**: Verify editing a section of a published course downgrades its status.
*   **Method**: PUT
*   **URL**: http://localhost:8080/api/v1/instructors/courses/<COURSE_ID>/sections/<SECTION_ID>
*   **Headers**: Authorization: Bearer <INSTRUCTOR_JWT>
*   **Body**: 
    `json
    {
      "title": "Updated Section Title",
      "description": "Updated Description",
      "displayOrder": 1
    }
    `
*   **Expected Status**: 200 OK
*   **Expected DB Effect**: The course's publish_status in the courses table will transition back from PUBLISHED to PENDING_APPROVAL.

## 3. Section Reordering
**Objective**: Atomically update section displayOrder.
*   **Method**: PATCH
*   **URL**: http://localhost:8080/api/v1/instructors/courses/<COURSE_ID>/sections/reorder
*   **Headers**: Authorization: Bearer <INSTRUCTOR_JWT>
*   **Body**:
    `json
    {
      "orderedSectionIds": [
        "<SECTION_ID_2>",
        "<SECTION_ID_1>"
      ]
    }
    `
*   **Expected Status**: 200 OK

## 4. Lesson Reordering
**Objective**: Atomically update lesson displayOrder.
*   **Method**: PATCH
*   **URL**: http://localhost:8080/api/v1/instructors/courses/<COURSE_ID>/sections/<SECTION_ID>/lessons/reorder
*   **Headers**: Authorization: Bearer <INSTRUCTOR_JWT>
*   **Body**:
    `json
    {
      "orderedLessonIds": [
        "<LESSON_ID_2>",
        "<LESSON_ID_1>"
      ]
    }
    `
*   **Expected Status**: 200 OK

## 5. Course Soft Archiving
**Objective**: Archive a course instead of hard-deleting it.
*   **Method**: PUT
*   **URL**: http://localhost:8080/api/v1/instructors/courses/<COURSE_ID>/archive
*   **Headers**: Authorization: Bearer <INSTRUCTOR_JWT>
*   **Expected Status**: 200 OK

## 6. Course Safe Deletion Constraint
**Objective**: Ensure courses with existing enrollments cannot be hard-deleted.
*   **Method**: DELETE
*   **URL**: http://localhost:8080/api/v1/instructors/courses/<COURSE_ID_WITH_ENROLLMENTS>
*   **Headers**: Authorization: Bearer <INSTRUCTOR_JWT>
*   **Expected Status**: 400 Bad Request (Validation Error)
*   **Expected Response**: "Cannot delete a course with active enrollments or payments. Please archive it instead."
