# API Documentation

## Phase 6 - Progress Tracking

### Update Lesson Progress
`POST /api/v1/courses/{courseId}/lessons/{lessonId}/progress`
Updates a student's progress for a specific lesson. For video lessons, setting the position beyond the completion threshold (95% of duration) marks the lesson as complete. For text lessons, any progress update marks the lesson as complete.

**Request Body:**
```json
{
  "positionSeconds": 120
}
```

### Get Course Progress
`GET /api/v1/courses/{courseId}/progress`
Retrieves a student's progress for an enrolled course, including overall completion percentage and individual lesson progress details.

**Response Body:**
```json
{
  "success": true,
  "data": {
    "courseId": "uuid",
    "totalLessons": 10,
    "completedLessons": 4,
    "completionPercentage": 40.0,
    "lastAccessedLessonId": "uuid",
    "lastAccessedAt": "2023-01-01T12:00:00",
    "lessonProgress": [
      {
        "lessonId": "uuid",
        "status": "COMPLETED",
        "lastPositionSeconds": 120,
        "maxPositionSeconds": 120,
        "completedAt": "2023-01-01T12:00:00"
      }
    ]
  }
}
```
