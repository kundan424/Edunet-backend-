# Implementation Plan — EdTech Platform

## Phase Status Overview

| Phase | Name | Status |
|-------|------|--------|
| 0 | Greenfield Initialization | ✅ Complete |
| 1 | Authentication & User Management | ⏳ Pending |
| 2 | Instructor Onboarding & Verification | ⏳ Pending |
| 3 | Course & Curriculum Management | ⏳ Pending |
| 4 | Course Discovery | ⏳ Pending |
| 5 | Free Enrollment & Access Control | ⏳ Pending |
| 6 | Progress Tracking | ⏳ Pending |
| 7 | Quiz System | ⏳ Pending |
| 8 | Assignment System | ⏳ Pending |
| 9 | Stripe Payment System | ⏳ Pending |
| 10 | Paid Enrollment | ⏳ Pending |
| 11 | Media Upload | ⏳ Pending |
| 12 | Video Processing & HLS | ⏳ Pending |
| 13 | Video Playback | ⏳ Pending |
| 14 | Reviews & Ratings | ⏳ Pending |
| 15 | Notifications | ⏳ Pending |
| 16 | Admin & Moderation | ⏳ Pending |
| 17 | Event-Driven Improvements | ⏳ Pending |
| 18 | Caching & Search Improvements | ⏳ Pending |
| 19 | Deployment | ⏳ Pending |
| 20 | Final Engineering Review | ⏳ Pending |

## Phase 0 — Greenfield Initialization

**Goal**: Spring Boot foundation. No business logic.

**Delivered**:
- Maven project (Java 21, Spring Boot 3.3.x)
- PostgreSQL + Flyway baseline
- Common exception framework
- API response wrappers
- Security configuration (permissive, Phase 1 locks it)
- RequestId correlation filter
- Health endpoint
- Module package stubs (13 modules)
- Test infrastructure (Testcontainers)
- Documentation foundation
- Docker Compose (PostgreSQL)

## Phase 1 — Authentication & User Management

**Goal**: Secure registration, login, JWT, RBAC foundation.

**Design & Changes**:
- **Database**: `V2__create_users_table.sql` creating the `users` table with UUID, unique email, password hash, role, status, and timestamps.
- **Domain**: `User` entity, `Role` enum (STUDENT, INSTRUCTOR, ADMIN), `UserStatus` enum (ACTIVE, INACTIVE, SUSPENDED).
- **Registration Policy**: `POST /api/v1/auth/register` allows normal users to register as `STUDENT` or `INSTRUCTOR`. Attempts to register as `ADMIN` are strictly blocked via validation and service-level enforcement.
- **Authentication**: `POST /api/v1/auth/login` uses `AuthenticationManager` to verify BCrypt hashes and returns a signed JWT.
- **Security Context**: `SecurityConfig` locked down. `JwtAuthenticationFilter` intercepts requests to validate stateless JWTs and populate `SecurityContextHolder`.
- **User Profile**: `GET /api/v1/users/me` returns the authenticated user's profile (safely excluding password hashes).
- **Tests**: Integration tests using local PostgreSQL to verify persistence, duplicate email constraints, JWT validation, and RBAC enforcement.

## Phase 2 — Instructor Onboarding and Verification

**Goal**: Dedicated instructor profiles and admin verification state machine.

**Design & Changes**:
- **Database**: `V3__create_instructor_profiles.sql` creates `instructor_profiles` linked to `users` 1:1.
- **Domain**: `InstructorProfile` entity, `VerificationStatus` enum (`UNVERIFIED`, `PENDING`, `VERIFIED`, `REJECTED`).
- **Instructor API (`/api/v1/instructors/**`)**: Authenticated INSTRUCTOR users can create/update their profile and request verification. Security context is used to enforce ownership (no ID passed in path).
- **Admin API (`/api/v1/admin/instructors/**`)**: Authenticated ADMIN users can list `PENDING` profiles, and explicitly approve or reject them.
- **State Machine**:
  - Profile Creation -> `UNVERIFIED`
  - Request Verification -> transitions `UNVERIFIED` or `REJECTED` to `PENDING`
  - Admin Verify -> `PENDING` to `VERIFIED`
  - Admin Reject -> `PENDING` to `REJECTED`
- **Tests**: Integration tests covering creation, duplicate prevention, verification request, admin approval/rejection, and RBAC negative cases (e.g., STUDENT accessing admin endpoints).
## Phase 3 - Course & Curriculum Management

**Goal**: Build the instructor course-authoring system (Course -> Section -> Lesson) using a strict structural hierarchy.

**Design & Changes**:
- **Database**: V4__create_courses_table.sql, V5__create_sections_table.sql, V6__create_lessons_table.sql.
- **Domain**: Course, Section, Lesson entities with cascading relationships (ON DELETE CASCADE). Publish Status defaults to DRAFT.
- **APIs**:
  - POST /api/v1/instructors/courses
  - PUT /api/v1/instructors/courses/{courseId}
  - POST /api/v1/instructors/courses/{courseId}/sections
  - POST /api/v1/instructors/courses/{courseId}/sections/{sectionId}/lessons
  - POST /api/v1/instructors/courses/{courseId}/submit
- **Security**: Strict ownership enforcement derived entirely from the @AuthenticationPrincipal JWT payload.
- **Business Rules**:
  - Only VERIFIED instructors can submit courses.
  - A course must have at least 1 section and 1 lesson to be submitted.

## Phase 4 - Course Discovery (Planned)

**Goal**: Implement public-facing APIs for discovering and viewing published courses with search, filtering, sorting, and pagination.

**Design**:
- **Endpoints**:
  - GET /api/v1/courses (List, Search, Filter)
  - GET /api/v1/courses/{courseId} (Detail, Curriculum Preview)
- **Data Filtering**:
  - Leverage org.springframework.data.jpa.domain.Specification for dynamic dynamic querying on the DB layer.
  - Hardcode publishStatus = PUBLISHED.
- **N+1 Prevention**:
  - List endpoint: Fetch instructor names via bulk IN query.
  - Detail endpoint: Use @EntityGraph or JOIN FETCH to eagerly load Sections and Lessons cleanly.
- **Security**: Do not leak sensitive user info, password hashes, or media URLs.
