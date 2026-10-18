# Edunet — Full-Featured EdTech & Learning Management Platform (Backend API)

Edunet is a multi-role online learning platform backend designed for students, instructors, and platform administrators. It powers the end-to-end lifecycle of digital education—from instructor verification, hierarchical course authoring, and cloud media storage to Stripe-backed enrollments, interactive quizzes, graded assignments, real-time progress tracking, and event-driven notifications.

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?style=flat-square&logo=flyway&logoColor=white)
![Stripe](https://img.shields.io/badge/Stripe-Payments-635BFF?style=flat-square&logo=stripe&logoColor=white)
![Backblaze B2](https://img.shields.io/badge/Backblaze_B2-Cloud_Storage-E21E26?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat-square&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI-2088FF?style=flat-square&logo=githubactions&logoColor=white)

## Links & Verification

* **Live API Base URL:** [api-url](https://edtech-g1wd.onrender.com)
* **Interactive API Documentation (Swagger UI):** `http://localhost:8080/swagger-ui/index.html`
* **OpenAPI Specification:** `http://localhost:8080/v3/api-docs`
* **Frontend Repository:** [Edunet Frontend](https://github.com/kundan424/Edunet.git)
* **CI/CD Pipeline:** [GitHub Actions Workflow](.github/workflows/ci.yml)

### API & Workflow Previews

> **Note:** Store your screenshots inside `docs/images/` in the repository so relative links render directly on GitHub.

| Swagger UI / OpenAPI Endpoints | Course & Payment Flow (Postman) |
| :---: | :---: |
| ![Swagger UI](docs/images/swagger-overview.png) | ![Checkout & Webhook Flow](docs/images/stripe-checkout-flow.png) |
| *Interactive REST API documentation grouped by domain module* | *End-to-end course enrollment via Stripe webhook verification* |

## Problem Statement & Overview

Building an online learning platform requires more than serving static video links and basic CRUD endpoints. Real-world educational platforms must coordinate multiple user roles with strict trust boundaries: instructors need a structured way to author hierarchical curriculums and upload media without exposing paid content to unauthenticated users; students need reliable payment processing, interactive assessments, and granular progress tracking; and administrators need moderation controls to verify instructors and review courses before they go live.

**Edunet** addresses these challenges by providing a modular, event-driven Spring Boot backend that manages the complete lifecycle of an online course marketplace and learning management system (LMS):

* **Controlled Content Supply (Trust & Moderation):** Users cannot arbitrarily publish courses. Instructors must submit a profile for verification (`VerificationStatus`), and courses go through a formal moderation state machine (`PublishStatus`) reviewed by administrators (`AdminCourseModerationService`) before becoming discoverable in the public catalog.
* **Structured Curriculum & Protected Media Delivery:** Courses are organized hierarchically into ordered **Sections** and **Lessons**. Media assets (`MediaAsset`) are abstracted behind a storage interface supporting both local development storage and **Backblaze B2** cloud object storage, separating public course previews (`PublicLessonResponse`) from enrolled-student learning streams (`CourseLearningResponse`).
* **Idempotent Monetization & Enrollment:** Paid course enrollments are handled through **Stripe Checkout** and verified asynchronously via `StripeWebhookController`. To prevent duplicate enrollments or double-processing on webhook retries, incoming Stripe events are tracked in a dedicated `StripeEvent` table before publishing a `PaymentSucceededEvent`.
* **Interactive Assessments & Progress Tracking:** Beyond video and text lessons, the platform supports auto-evaluated quizzes (`QuizTakingService`) and instructor-graded assignments (`AssignmentGradingService`), alongside per-lesson progress tracking (`LessonProgress`) and role-specific analytics dashboards.
* **Decoupled Notification Engine:** Core domain actions—such as course approval/rejection, instructor verification, enrollment confirmation, and assignment grading—publish Spring domain events consumed by `NotificationEventListener` to dispatch both in-app notifications and transactional emails (via **Resend** or mock local providers) while respecting user email preferences (`EmailPreferenceDTO`).

## Features & Capabilities

### 1. Authentication, Authorization & User Management
* **Stateless JWT Security:** User registration and login (`AuthController`) backed by Spring Security and custom `JwtAuthenticationFilter` for stateless request authentication.
* **Role-Based Access Control (RBAC):** Fine-grained access boundaries across `STUDENT`, `INSTRUCTOR`, and `ADMIN` roles (`Role`), alongside account status tracking (`UserStatus`).
* **Instructor Onboarding & Verification:** Aspiring instructors can create and update professional profiles (`InstructorProfileService`) and submit them for administrative verification (`VerificationStatus`) before publishing courses.
* **Configurable Email Preferences:** Users can manage granular notification opt-in/opt-out settings (`EmailPreferenceDTO`).

### 2. Course Authoring, Discovery & Reviews
* **Hierarchical Curriculum Builder:** Instructors can author courses (`CourseService`), organize syllabus structure into ordered **Sections** (`SectionService`), and populate them with **Lessons** (`LessonService`) supporting custom drag-and-drop reordering (`SectionReorderRequest`, `LessonReorderRequest`).
* **Multi-Attribute Catalog Search:** Public course discovery (`CourseDiscoveryController`) powered by dynamic JPA Specifications (`CourseSpecification`) supporting keyword search, difficulty filtering (`CourseDifficulty`), and pagination (`PageResponse`).
* **Public vs. Enrolled Content Isolation:** Unauthenticated visitors can browse course summaries and syllabus previews (`PublicSectionResponse`, `PublicLessonResponse`), while actual learning media and full lesson details are restricted to enrolled students (`CourseLearningResponse`).
* **Ratings & Course Reviews:** Enrolled students can submit ratings and written reviews (`CourseReviewController`, `CourseReviewService`) to build social proof across the catalog.

### 3. Payments, Enrollments & Learning Progress
* **Stripe Checkout Integration:** Students can initiate checkout sessions (`StudentPaymentController`) for paid courses, with transaction states tracked via `PaymentStatus`.
* **Idempotent Webhook Processing:** Asynchronous payment confirmation via `StripeWebhookController` and `StripeWebhookService`, recording processed event IDs (`StripeEvent`) to prevent duplicate enrollments on webhook retries.
* **Automated Course Enrollment:** Confirmed payments trigger instant enrollment (`EnrollmentService`), unlocking the student's learning view.
* **Granular Progress Tracking:** Students can update and track completion states per lesson (`ProgressController`, `LessonProgress`) and view aggregated course completion percentages (`CourseProgressResponse`).

### 4. Interactive Assessments (Quizzes & Assignments)
* **Quiz Authoring & Auto-Grading:** Instructors can build multi-question quizzes with multiple-choice options (`QuizAuthoringService`). Students can take timed/tracked quiz attempts (`QuizTakingService`, `StudentQuizController`) with automatic score calculation (`QuizAttempt`, `QuizAttemptAnswer`) while hiding correct-answer flags in student-facing DTOs (`StudentOptionResponse`).
* **Assignment Submissions & Instructor Grading:** Instructors can attach coursework assignments to lessons (`AssignmentAuthoringService`), and students can submit their work (`AssignmentSubmissionService`). Instructors can review submissions, assign grades, and provide written feedback (`AssignmentGradingService`, `InstructorAssignmentGradingController`).

### 5. Cloud Media Management
* **Pluggable Storage Architecture:** Unified `MediaStorageService` interface with environment-driven switching between local filesystem storage (`LocalMediaStorageServiceImpl`) for development and **Backblaze B2** cloud storage (`B2MediaStorageServiceImpl`) for production.
* **Role-Scoped Media Access:** Separate upload/management endpoints for instructors (`InstructorMediaController`) and protected media retrieval endpoints for enrolled students (`StudentMediaController`), backed by `MediaAsset` metadata and `ProcessingStatus` tracking.

### 6. Event-Driven Notifications & Role Dashboards
* **Asynchronous In-App & Email Notifications:** Domain events (`CoursePublishedEvent`, `CourseRejectedEvent`, `InstructorVerifiedEvent`, `CourseEnrollmentEvent`, `AssignmentGradedEvent`, `QuizCompletedEvent`, `PaymentSucceededEvent`) are handled by `NotificationEventListener` to persist in-app alerts (`NotificationController`) and dispatch emails via **Resend** (`ResendEmailServiceImpl`) or a local mock provider (`MockEmailService`).
* **Role-Specific Analytics Dashboards:** Dedicated aggregation endpoints (`DashboardController`) serving tailored metrics for:
  * **Students:** Enrolled courses, "Continue Learning" resume points (`ContinueLearningDTO`), and recent learning activity (`StudentDashboardResponse`).
  * **Instructors:** Course-level analytics (`InstructorCourseAnalyticsResponse`), enrollment totals, and recent student reviews (`InstructorDashboardResponse`).
  * **Admins:** Platform-wide user, course moderation, and operational metrics (`AdminDashboardResponse`).

### 7. Platform Administration & Content Moderation
* **Course Moderation Workflow:** Administrators can review pending courses, approve them for publication, or reject them with structured feedback (`AdminCourseModerationController`, `CourseRejectionRequest`).
* **Instructor Verification & User Governance:** Admins can approve or reject instructor verification requests (`AdminVerificationController`) and manage platform users (`AdminUserController`).

## Technology Stack

| Layer / Responsibility | Technology | Role in the Application |
| :--- | :--- | :--- |
| **Core Backend Framework** | **Java 17+ & Spring Boot 3.x** | Provides dependency injection, REST API routing, validation, profile-based configuration (`dev`, `test`, `prod`), and Spring application events. |
| **Security & Auth** | **Spring Security & JWT** | Enforces stateless authentication (`JwtAuthenticationFilter`, `JwtUtils`), password hashing, CORS policies (`CorsConfig`), and role-based endpoint protection. |
| **Database & Persistence** | **PostgreSQL & Spring Data JPA** | Stores relational domain data across 16 schema versions; utilizes JPA entities, custom JPQL queries, and `JpaSpecificationExecutor` (`CourseSpecification`) for dynamic filtering. |
| **Schema Migrations** | **Flyway** | Version-controls database evolution across 16 incremental SQL migration scripts (`V1__baseline_schema.sql` through `V16__add_email_preferences.sql`). |
| **Payment Processing** | **Stripe SDK & Webhooks** | Manages checkout session creation and cryptographically verified webhook event handling for course purchases. |
| **Cloud Object Storage** | **Backblaze B2 / Local FS** | Stores uploaded course media assets behind the `MediaStorageService` strategy interface. |
| **Transactional Email** | **Resend API / Mock Service** | Dispatches event-driven transactional emails via `ResendEmailServiceImpl` in production and `MockEmailService` in local/test environments. |
| **Observability & Tracing** | **RequestIdFilter (MDC/Logging)** | Injects and propagates a unique correlation/request ID per HTTP request for structured debugging and error responses (`ErrorResponse`). |
| **Containerization & Proxy** | **Docker, Docker Compose & Nginx** | Packages the Spring Boot application (`Dockerfile`), orchestrates multi-container local and production stacks (`docker-compose.yml`, `docker-compose.prod.yml`), and configures reverse-proxy routing (`nginx.conf`). |
| **Testing & CI** | **JUnit 5, MockMvc & GitHub Actions** | Runs unit and slice/integration tests across controllers, services, and repositories (`src/test/`), automated on every push via `.github/workflows/ci.yml`. |

## Architecture & Data Flow

Edunet is structured as a **Domain-Driven Modular Monolith** built on Spring Boot. Rather than grouping all classes into global technical layers (`controllers/`, `services/`, `repositories/`), the codebase is partitioned into 12 cohesive domain packages (`auth`, `user`, `course`, `enrollment`, `progress`, `quiz`, `assignment`, `payment`, `media`, `notification`, `dashboard`, and `admin`) alongside a shared `common` infrastructure module. Cross-domain side effects—such as triggering enrollments after payment or sending emails after course moderation—are decoupled using **Spring Application Events**.

### 1. High-Level System Architecture

#### Option A: GitHub Mermaid Diagram (Renders automatically on GitHub & imports into Excalidraw)
![architecture diagram](docs/images/architecture.svg)
### 2. Core Request & Data Flows

#### Flow 1: Idempotent Course Purchase & Automated Enrollment
When a student purchases a paid course, Edunet avoids trusting client-side redirects and instead relies on cryptographically signed Stripe webhooks with database-level idempotency:

[flow diagram](docs/images/flowdiagram.svg)

**Step-by-step data flow:**
1. `StudentPaymentController` receives the checkout request and delegates to `PaymentService`, which creates a `PENDING` `Payment` entity and returns a Stripe Checkout URL (`CheckoutResponseDTO`).
2. Upon payment completion, Stripe sends an asynchronous webhook to `StripeWebhookController`.
3. `StripeWebhookService` verifies the webhook signature and queries `StripeEventRepository` to check if the Stripe event ID has already been processed.
4. If the event is new, it persists the `StripeEvent`, marks the `Payment` as `SUCCEEDED`, and publishes a `PaymentSucceededEvent`.
5. The enrollment flow activates the student's `Enrollment` and emits a `CourseEnrollmentEvent`, which `NotificationEventListener` consumes to create an in-app notification (`Notification`) and send a confirmation email via `EmailService` (if enabled in the user's `EmailPreferenceDTO`).

---

#### Flow 2: Course Authoring, Admin Moderation & Public Discovery
1. **Instructor Verification:** A user submits an `InstructorProfileRequest` via `InstructorController`. An administrator reviews and approves the profile via `AdminVerificationController`, transitioning `VerificationStatus` to `VERIFIED` and emitting an `InstructorVerifiedEvent`.
2. **Drafting Curriculum:** The verified instructor creates a course (`CourseController`), adds ordered sections (`SectionController`), uploads media assets (`InstructorMediaController` $\rightarrow$ `B2MediaStorageServiceImpl` / `LocalMediaStorageServiceImpl`), and attaches quizzes (`InstructorQuizController`) or assignments (`InstructorAssignmentController`).
3. **Moderation Gate:** When submitted for review, an administrator evaluates the course via `AdminCourseModerationController`:
  * **Approval:** `AdminCourseModerationService` transitions the course's `PublishStatus` to `PUBLISHED` and emits a `CoursePublishedEvent`.
  * **Rejection:** Transitions status to `REJECTED` with a required reason (`CourseRejectionRequest`) and emits a `CourseRejectedEvent`.
4. **Notification & Catalog Indexing:** `NotificationEventListener` notifies the instructor of the outcome. Published courses immediately become queryable through `CourseDiscoveryController` via `CourseSpecification` filters.

---

### 3. Component Design & Responsibilities

* **Controllers (`*/controller/*`):** Handle HTTP routing, request parameter binding, `@Valid` payload validation, and role enforcement. Controllers never touch JPA entities directly; they accept request DTOs and return standardized `ApiResponse<T>` or `PageResponse<T>` wrappers.
* **Services (`*/service/*`):** Enforce domain invariants (e.g., verifying that an instructor owns a course before modifying sections, checking enrollment status before granting access to lesson media, or auto-grading a `QuizAttempt`).
* **Repositories (`*/repository/*`):** Spring Data JPA interfaces encapsulating persistence operations, custom JPQL aggregations (such as `AdminDashboardRepository`, `InstructorDashboardRepository`, and `StudentDashboardRepository`), and dynamic query specifications (`CourseSpecification`).
* **DTOs (`*/dto/*`):** Strict separation between internal persistence models and external API contracts. Notably, assessment and course modules maintain role-segregated DTOs—such as `InstructorOptionResponse` (includes `isCorrect`) vs. `StudentOptionResponse` (omits answer keys), and `PublicLessonResponse` vs. `CourseLearningResponse`.
* **Events & Listeners (`*/event/*`, `notification/listener/*`):** Lightweight immutable event records that decouple core database transactions from notification and email delivery side effects.

---

### 4. Engineering Decisions & Trade-Offs

* **Modular Monolith over Microservices:**
  * *Decision:* Packaged the 12 domains inside a single deployable Spring Boot artifact with strict package boundaries rather than splitting them into separate microservices.
  * *Trade-off:* Avoids distributed transaction complexity (2PC/Sagas) between payments, enrollments, and course progress while maintaining strong ACID guarantees in PostgreSQL. Domain events (`ApplicationEventPublisher`) keep modules loosely coupled so individual modules (like `notification` or `media`) could be extracted in the future if independent scaling is needed.
* **Strategy Pattern for External Providers (Media & Email):**
  * *Decision:* Abstracted file storage behind `MediaStorageService` (`B2MediaStorageServiceImpl` vs. `LocalMediaStorageServiceImpl`) and email delivery behind `EmailService` (`ResendEmailServiceImpl` vs. `MockEmailService`).
  * *Trade-off:* Allows local development and automated CI integration tests to run with zero external cloud credentials or network flakiness, while production uses Backblaze B2 and Resend seamlessly via configuration properties.
* **Webhook Idempotency via Dedicated `StripeEvent` Table:**
  * *Decision:* Stored every processed Stripe webhook event ID in PostgreSQL (`V12__create_payments.sql`) rather than relying solely on in-memory caches.
  * *Trade-off:* Adds a lightweight database lookup per webhook call, but guarantees durable idempotency across container restarts and prevents duplicate enrollments when Stripe retries webhook delivery.
* **Role-Segregated DTO Projections:**
  * *Decision:* Created separate DTO hierarchies for public visitors, students, instructors, and admins instead of reusing a single DTO with `@JsonIgnore` flags.
  * *Trade-off:* Increases the number of DTO classes to maintain, but eliminates accidental data leakage (such as exposing quiz answer keys or unpurchased lesson media URLs).

## Project Structure

```text
edunet-backend/
├── .github/workflows/
│   └── ci.yml                          # GitHub Actions CI pipeline for automated builds & tests
├── src/
│   ├── main/
│   │   ├── java/com/edtech/platform/
│   │   │   ├── EdTechApplication.java  # Spring Boot entry point
│   │   │   ├── admin/                  # Course moderation, instructor verification & user governance
│   │   │   ├── assignment/             # Assignment authoring, student submissions & instructor grading
│   │   │   ├── auth/                   # Registration, login & token issuance
│   │   │   ├── common/                 # Shared infrastructure
│   │   │   │   ├── exception/          # GlobalExceptionHandler, ErrorCode & custom domain exceptions
│   │   │   │   ├── response/           # Standardized ApiResponse, ErrorResponse & PageResponse wrappers
│   │   │   │   ├── security/           # SecurityConfig, JwtAuthenticationFilter, JwtUtils & CORS
│   │   │   │   └── util/               # RequestIdFilter (MDC correlation IDs) & Constants
│   │   │   ├── course/                 # Courses, Sections, Lessons, Reviews & dynamic JPA Specifications
│   │   │   ├── dashboard/              # Aggregated analytics for Student, Instructor & Admin dashboards
│   │   │   ├── email/                  # EmailService interface, Resend API integration & Mock provider
│   │   │   ├── enrollment/             # Student course enrollments & protected learning views
│   │   │   ├── media/                  # MediaAsset management with Backblaze B2 & Local storage strategies
│   │   │   ├── notification/           # NotificationEventListener, in-app alerts & email dispatch
│   │   │   ├── payment/                # Stripe Checkout integration & idempotent StripeWebhookService
│   │   │   ├── progress/               # Per-lesson completion tracking & course progress aggregation
│   │   │   ├── quiz/                   # Quiz authoring, timed/tracked student attempts & auto-grading
│   │   │   └── user/                   # User management, InstructorProfile & email preferences
│   │   └── resources/
│   │       ├── db/migration/           # 16 Flyway SQL migrations (V1__baseline to V16__email_preferences)
│   │       ├── application.yml         # Base configuration
│   │       ├── application-dev.yml     # Local development profile
│   │       └── application-test.yml    # Isolated test profile
│   └── test/                           # Unit & integration test suites mirroring domain packages
├── Dockerfile                          # Multi-stage container build definition
├── docker-compose.yml                  # Local development stack (App + PostgreSQL)
├── docker-compose.prod.yml             # Production container orchestration
├── nginx.conf                          # Nginx reverse proxy configuration
├── .env.example                        # Template for required environment variables
└── pom.xml                             # Maven dependencies and build configuration
```
## Getting Started

### Prerequisites
Ensure you have the following installed on your machine before running the project:
* **Java Development Kit (JDK):** Java 17 or higher
* **Maven:** 3.8+ (or use the included Maven Wrapper `./mvnw` / `mvnw.cmd`)
* **Database:** PostgreSQL 15+ (or **Docker & Docker Compose** to spin up the database/application containers automatically)
* **Optional External Accounts (for production/cloud providers):**
  * **Stripe Account & Stripe CLI:** For checkout sessions and local webhook forwarding
  * **Backblaze B2 Bucket:** If switching media storage from `local` to cloud object storage
  * **Resend API Key:** If switching email delivery from `mock` to live transactional emails

---

### Installation

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/kundan424/edunet-backend-.git](https://github.com/kundan424/edunet-backend-.git)
   cd edunet-backend-
   ```

2. **Create your environment configuration file:**
   Copy the provided `.env.example` template to `.env` in the root directory:
   ```bash
   cp .env.example .env
   ```

---

### Configuration & Environment Variables

The application uses environment variables defined in `.env` (or injected via Docker Compose / OS environment) mapped into `application.yml` and `application-dev.yml`.

Below are the key configuration variables (use safe placeholders locally; never commit real secrets):

| Variable Category | Variable Name | Description / Default Behaviour |
| :--- | :--- | :--- |
| **Spring Profile** | `SPRING_PROFILES_ACTIVE` | Active profile (`dev`, `test`, or `prod`). Defaults to `dev` for local development. |
| **Database (PostgreSQL)** | `DB_URL` / `SPRING_DATASOURCE_URL` | JDBC connection URL (e.g., `jdbc:postgresql://localhost:5432/edtech_db`). |
| | `DB_USERNAME` / `DB_PASSWORD` | PostgreSQL database credentials. |
| **Security (JWT)** | `JWT_SECRET` | Base64-encoded secret key used by `JwtUtils` to sign and verify access tokens. |
| | `JWT_EXPIRATION_MS` | Token validity duration in milliseconds. |
| **Payments (Stripe)** | `STRIPE_API_KEY` | Stripe secret key (`sk_test_...`) used by `PaymentService`. |
| | `STRIPE_WEBHOOK_SECRET` | Webhook signing secret (`whsec_...`) used by `StripeWebhookService` to verify events. |
| **Media Storage** | `MEDIA_STORAGE_PROVIDER` | Storage strategy selector (`local` for `LocalMediaStorageServiceImpl` or `b2` for `B2MediaStorageServiceImpl`). |
| | `B2_KEY_ID` / `B2_APPLICATION_KEY` | Backblaze B2 credentials (required only when using `b2` storage). |
| | `B2_BUCKET_NAME` / `B2_ENDPOINT` | Target Backblaze B2 bucket name and S3-compatible endpoint. |
| **Email Service** | `EMAIL_PROVIDER` | Email strategy selector (`mock` for `MockEmailService` console logging or `resend` for `ResendEmailServiceImpl`). |
| | `RESEND_API_KEY` | Resend API key (required only when live email delivery is enabled). |

> **Database Migrations Note:** You do **not** need to run SQL scripts manually. When the application starts, **Flyway** automatically executes all pending migrations (`V1__baseline_schema.sql` through `V16__add_email_preferences.sql`) against the configured PostgreSQL database.

---

### Running the Application

#### Option 1: Run Everything with Docker Compose (Recommended)
Start both the PostgreSQL database and the Spring Boot backend using the local compose file:

```bash
docker compose up --build
```
* The PostgreSQL instance will initialize first, and the backend will automatically apply Flyway migrations (`V1`–`V16`) on startup.
* The API will be available at `http://localhost:8080`.

#### Option 2: Local Development (PostgreSQL in Docker + Maven Wrapper)
If you want fast live-reloading while coding in your IDE:

1. **Start only the PostgreSQL container:**
   ```bash
   docker compose up -d postgres
   ```
   *(Or ensure a local PostgreSQL server is running and matches your `.env` credentials).*

2. **Compile and start the Spring Boot server using the Maven Wrapper:**
  * **Linux / macOS:**
    ```bash
    ./mvnw clean spring-boot:run
    ```
  * **Windows (Command Prompt / PowerShell):**
    ```powershell
    .\mvnw.cmd clean spring-boot:run
    ```

3. **Verify the server is running:**
   Open Swagger UI at `http://localhost:8080/swagger-ui/index.html` or test the health/discovery endpoint:
   ```bash
   curl http://localhost:8080/api/v1/courses
   ```

#### Option 3: Testing Stripe Webhooks Locally (Optional)
To test the end-to-end payment and enrollment flow locally (`StripeWebhookController`), forward Stripe events using the Stripe CLI:
```bash
stripe listen --forward-to localhost:8080/api/v1/webhooks/stripe
```
## API Documentation & Usage Examples

All REST endpoints return a standardized JSON envelope (`ApiResponse<T>` for single resources, `PageResponse<T>` for paginated collections, and `ErrorResponse` via `GlobalExceptionHandler` when a domain or validation error occurs). Every response is tagged with a unique correlation ID generated by `RequestIdFilter`.

> **Interactive Reference:** When the application is running locally, explore the complete API specification via Swagger UI at `http://localhost:8080/swagger-ui/index.html`.

### Core API Modules Overview

| Domain Module | Controller Class(es) | Primary Capabilities | Access Level |
| :--- | :--- | :--- | :--- |
| **Authentication** | `AuthController` | User registration (`RegisterRequest`) and JWT login (`LoginRequest` $\rightarrow$ `LoginResponse`). | Public |
| **Users & Profiles** | `UserController`, `InstructorController` | Current user profile (`UserResponse`), email preferences (`EmailPreferenceDTO`), and instructor onboarding (`InstructorProfileRequest`). | Authenticated / Instructor |
| **Catalog Discovery** | `CourseDiscoveryController` | Dynamic course search & filtering (`CourseSearchRequest`), public syllabus previews (`PublicSectionResponse`, `PublicLessonResponse`). | Public |
| **Course Authoring** | `CourseController`, `SectionController`, `LessonController` | Course creation/updates, submitting courses for moderation, and section/lesson CRUD with drag-and-drop reordering (`SectionReorderRequest`, `LessonReorderRequest`). | `INSTRUCTOR` |
| **Reviews & Ratings** | `CourseReviewController` | Submitting and retrieving student course ratings and written reviews (`CourseReviewRequest`, `CourseReviewResponse`). | Public (Read) / Enrolled `STUDENT` (Write) |
| **Payments & Webhooks** | `StudentPaymentController`, `StripeWebhookController` | Initiating Stripe Checkout sessions (`CheckoutResponseDTO`) and processing signed Stripe webhooks (`Stripe-Signature`). | `STUDENT` / Stripe Server |
| **Enrollments & Learning** | `EnrollmentController`, `ProgressController` | Fetching enrolled courses, full learning curriculum (`CourseLearningResponse`), and updating per-lesson completion (`ProgressUpdateRequest`). | `STUDENT` |
| **Quizzes** | `InstructorQuizController`, `StudentQuizController` | Authoring quizzes/questions/options (`QuizCreateRequest`) and submitting student quiz attempts for auto-grading (`QuizSubmitRequest` $\rightarrow$ `QuizAttemptResponse`). | `INSTRUCTOR` / `STUDENT` |
| **Assignments** | `InstructorAssignmentController`, `StudentAssignmentController`, `InstructorAssignmentGradingController` | Creating assignments, submitting student coursework (`AssignmentSubmissionRequest`), and grading submissions with feedback (`GradeSubmissionRequest`). | `INSTRUCTOR` / `STUDENT` |
| **Media Assets** | `InstructorMediaController`, `StudentMediaController` | Uploading lesson media to Backblaze B2 / Local storage and serving protected media streams (`MediaAssetDto`). | `INSTRUCTOR` / Enrolled `STUDENT` |
| **Notifications** | `NotificationController` | Listing in-app notifications (`NotificationResponse`) and marking alerts as read. | Authenticated |
| **Dashboards** | `DashboardController` | Role-specific analytics: `StudentDashboardResponse`, `InstructorDashboardResponse`, `InstructorCourseAnalyticsResponse`, and `AdminDashboardResponse`. | `STUDENT` / `INSTRUCTOR` / `ADMIN` |
| **Administration** | `AdminCourseModerationController`, `AdminVerificationController`, `AdminUserController` | Approving/rejecting courses (`CourseRejectionRequest`), verifying instructors, and governing user accounts (`UserSummaryResponse`). | `ADMIN` |

---

### Representative Request & Response Examples

#### 1. Authenticate & Obtain JWT (`POST /api/v1/auth/login`)
**Request:**
```http
POST /api/v1/auth/login HTTP/1.1
Content-Type: application/json

{
  "email": "student@edunet.com",
  "password": "StrongPassword123!"
}
```

**Response (`200 OK`):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "email": "student@edunet.com",
      "role": "STUDENT",
      "status": "ACTIVE"
    }
  }
}
```

#### 2. Submit a Quiz Attempt (`POST /api/v1/student/quizzes/{quizId}/submit`)
**Request:**
```http
POST /api/v1/student/quizzes/8f9d2a10-4c3b-41e0-9f1a-123456789abc/submit HTTP/1.1
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "answers": [
    {
      "questionId": "111e8400-e29b-41d4-a716-446655440001",
      "selectedOptionId": "222e8400-e29b-41d4-a716-446655440002"
    }
  ]
}
```

**Response (`200 OK`):**
```json
{
  "success": true,
  "message": "Quiz submitted and graded",
  "data": {
    "attemptId": "999e8400-e29b-41d4-a716-446655440999",
    "quizId": "8f9d2a10-4c3b-41e0-9f1a-123456789abc",
    "score": 100.0,
    "status": "COMPLETED",
    "submittedAt": "2026-10-14T18:00:00Z"
  }
}
```

#### 3. Standardized Error Format (`ErrorResponse`)
When a business rule fails (e.g., an unverified instructor attempts to publish a course, or a student tries to enroll twice), `GlobalExceptionHandler` returns a structured payload mapped to `ErrorCode`:
```json
{
  "success": false,
  "errorCode": "CONFLICT",
  "message": "Student is already enrolled in this course",
  "timestamp": "2026-10-14T18:05:12Z",
  "requestId": "req-7c9e6679-7425-40de-944b-e07fc1f90ae7"
}
```
## Database Design & Key Entities

The PostgreSQL schema is version-controlled across **16 incremental Flyway migrations** (`V1__baseline_schema.sql` through `V16__add_email_preferences.sql`), evolving cleanly across identity, curriculum, assessments, commerce, media, and notifications.

### 1. Entity-Relationship Diagram (ERD)

#### Option A: GitHub Mermaid ERD (Renders on GitHub)
![ER DIAGRAM](docs/images/schema.svg)
### 2. Core Domain Tables & Migration Breakdown

| Domain Area | Entities / Tables | Flyway Migration(s) | Key Constraints & Design Highlights |
| :--- | :--- | :--- | :--- |
| **Identity & Trust** | `User`, `InstructorProfile` | `V2`, `V3`, `V16` | Enforces unique email constraints, tracks `Role` (`STUDENT`, `INSTRUCTOR`, `ADMIN`), `UserStatus`, `VerificationStatus`, and per-user email notification flags (`V16`). |
| **Curriculum & Moderation** | `Course`, `Section`, `Lesson`, `CourseReview` | `V4`, `V5`, `V6`, `V7`, `V14`, `V15` | Hierarchical `Course` $\rightarrow$ `Section` $\rightarrow$ `Lesson` relationships with explicit ordering columns; indexed for catalog search (`V7`) and extended with admin moderation audit fields (`V15`). |
| **Enrollment & Progress** | `Enrollment`, `LessonProgress` | `V8`, `V9` | Prevents duplicate enrollments via composite uniqueness on `(user_id, course_id)`; tracks granular `ProgressStatus` per student per lesson. |
| **Assessments** | `Quiz`, `QuizQuestion`, `QuestionOption`, `QuizAttempt`, `QuizAttemptAnswer`, `Assignment`, `AssignmentSubmission` | `V10`, `V11` | Separates quiz definition (`QuizQuestion`, `QuestionOption`) from student execution (`QuizAttempt`, `QuizAttemptAnswer`) and tracks instructor grading workflows (`SubmissionStatus`). |
| **Commerce & Idempotency** | `Payment`, `StripeEvent` | `V12` | Links checkout sessions to `PaymentStatus` and stores processed Stripe webhook IDs in `StripeEvent` to guarantee idempotent webhook handling. |
| **Media & Notifications** | `MediaAsset`, `Notification` | `V13`, `V14` | Tracks storage keys and `ProcessingStatus` for uploaded files (`V13`) and persists event-driven in-app `Notification` records (`NotificationType`) alongside search indexes (`V14`). |

## Testing & Quality Checks

The repository includes an automated test suite (`src/test/java/com/edtech/platform/`) covering unit, service, repository, and end-to-end integration workflows under an isolated test profile (`application-test.yml`). Every push and pull request is verified automatically via **GitHub Actions** (`.github/workflows/ci.yml`).

### What is Tested

| Domain / Layer | Test Class(es) | Verification Scope |
| :--- | :--- | :--- |
| **Authentication & Users** | `AuthControllerTest`, `InstructorVerificationTest`, `EmailPreferencesIntegrationTest` | Registration/login endpoints, instructor verification state transitions, and user email preference persistence. |
| **Course Management & Discovery** | `CourseManagementTest`, `CourseDiscoveryTest`, `CourseReviewServiceTest` | Course/Section/Lesson CRUD, dynamic specification filtering, and student course review validation. |
| **Admin Moderation** | `AdminCourseModerationServiceTest`, `AdminCourseModerationIntegrationTest` | Course approval/rejection workflows, rejection reason enforcement, and moderation event publishing. |
| **Payments & Webhooks** | `PaymentServiceTest`, `StripeWebhookServiceTest`, `StripeWebhookIntegrationTest` | Checkout session creation, webhook signature handling, and database-backed `StripeEvent` idempotency. |
| **Enrollments & Progress** | `EnrollmentTest`, `ProgressServiceTest`, `ProgressIntegrationTest` | Post-payment enrollment activation, duplicate enrollment prevention, and lesson/course completion percentage calculation. |
| **Media Storage** | `MediaServiceTest`, `MediaAssetRepositoryTest`, `MediaStorageConfigurationTest`, `MediaStorageEnvironmentTest`, `MediaStorageOsEnvTest` | Storage provider selection (`local` vs. `b2`), environment property binding, and `MediaAsset` persistence. |
| **Notifications & Dashboards** | `NotificationServiceTest`, `EmailNotificationIntegrationTest`, `DashboardIntegrationTest` | Event-driven in-app notification creation, mock/Resend email dispatch, and role-based dashboard aggregations. |

### Running the Tests

Run the full unit and integration test suite using the Maven Wrapper:

* **Linux / macOS:**
  ```bash
  ./mvnw clean test
  ```
* **Windows (PowerShell / CMD):**
  ```powershell
  .\mvnw.cmd clean test
  ```
* **Run a specific test class:**
  ```bash
  ./mvnw test -Dtest=StripeWebhookIntegrationTest
  ```

  ## Deployment

The project includes production containerization support via a multi-stage `Dockerfile`, `docker-compose.prod.yml`, and `nginx.conf`:

```bash
docker compose -f docker-compose.prod.yml up -d --build
```
* **Containerized Runtime:** Builds an optimized Spring Boot JAR image and runs it alongside PostgreSQL.
* **Reverse Proxy:** `nginx.conf` routes incoming HTTP traffic to the internal Spring Boot container while forwarding client IP and protocol headers.
* **External Cloud Providers in Production:** Set `MEDIA_STORAGE_PROVIDER=b2` and `EMAIL_PROVIDER=resend` in the production environment to switch from local/mock implementations to Backblaze B2 object storage and Resend transactional emails.

---

## Known Limitations & Future Improvements

To keep the current scope focused and maintainable, the following limitations exist in the present implementation alongside planned next steps:

* **Synchronous Media Upload Processing:**
  * *Current Limitation:* Media uploads (`InstructorMediaController`) are handled within the application lifecycle and stored directly as objects without multi-bitrate video transcoding (HLS/DASH).
  * *Future Improvement:* Offload video transcoding to an asynchronous worker queue and generate signed, time-limited CDN streaming URLs.
* **In-Process Domain Events:**
  * *Current Limitation:* Domain events (`PaymentSucceededEvent`, `CoursePublishedEvent`, etc.) use Spring's in-memory `ApplicationEventPublisher`. If the process crashes mid-listener execution after a commit, non-transactional side effects (like external emails) do not automatically retry.
  * *Future Improvement:* Implement the **Transactional Outbox Pattern** or integrate a persistent message broker (RabbitMQ / Kafka) for guaranteed at-least-once notification delivery.
* **Database-Backed Catalog Search:**
  * *Current Limitation:* Course discovery (`CourseSpecification`) relies on PostgreSQL queries and indexes (`V7`, `V14`), which is sufficient for moderate catalog sizes but lacks typo-tolerant full-text ranking.
  * *Future Improvement:* Integrate PostgreSQL `tsvector` full-text search or an external search engine (Meilisearch / Elasticsearch) for fuzzy matching and faceted ranking.
* **Caching Layer:**
  * *Current Limitation:* Public catalog discovery and dashboard aggregations query PostgreSQL directly on each request.
  * *Future Improvement:* Introduce **Redis** caching for high-read public course details, curriculum trees, and instructor dashboard metrics with cache eviction on course updates.

---
