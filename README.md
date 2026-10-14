# EdTech Platform — Full-Stack E-Learning & Course Marketplace Engine

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0202?logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Stripe](https://img.shields.io/badge/Stripe-Payments-635BFF?logo=stripe&logoColor=white)](https://stripe.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)

EdTech Platform is an enterprise-grade learning management system and marketplace that bridges course authoring, video streaming, interactive assessments, and automated monetization. Built for students, verified instructors, and platform administrators, it delivers role-governed course enrollment, resume-capable video progress tracking, and secure Stripe checkouts backed by transactional data integrity.

## Links & Live Resources

| Resource | Link / Access | Description |
| :--- | :--- | :--- |
| **API Base URL** | `https://edtech-g1wd.onrender.com`  | Live Spring Boot REST API |
| **API Health & Actuator** | [`/actuator/health`](https://edtech-g1wd.onrender.com/actuator/health) | Live service status & database connectivity check |
---

## Visual Overview & Core Workflows

> Screenshots and workflow assets are tracked in [`docs/images/`](docs/images/).

### 1. Student Learning Journey (Video Stream & Heartbeat Progress)
Students experience gated media streaming with automatic progress persistence and real-time completion triggers once 95% of lesson content is consumed:


![Student Learning Experience](docs/images/student-classroom.png)
*Figure 1: Student classroom player displaying lesson streaming, section navigation, and completed progress markers.*

### 2. Instructor Studio (Curriculum Builder & Assessment Grading)
Instructors manage course publication pipelines, upload media, create quizzes, and review submitted assignments:

![Instructor Course Builder](docs/images/instructor-studio.png)
*Figure 2: Instructor management view for structuring modules, lessons, and grading student submissions.*

### 3. Admin Moderation & Verification Flow
Platform admins review instructor identity credentials and audit unpublished course content before public marketplace listing:

![Admin Course Moderation](docs/images/admin-dashboard.png)
*Figure 3: Admin portal showing instructor verification queues and course moderation controls.*

[//]: # (-------------------------------------------------------------------------------------------------------------------------------------------------------------------------)

## Problem Statement & Project Overview

### The Problem
A modern e-learning marketplace requires solving three major challenges that trivial implementations overlook:

1. **Unreliable Learning Progress Tracking:** Most simple LMS implementations mark course content as completed on superficial client clicks. This leads to inaccurate completion metrics, prevents students from resuming video playback across devices, and undermines certificate credibility.
2. **Payment Inconsistency & Concurrent Enrollment Conflicts:** Handling asynchronous payment flows (such as credit cards and localized payment methods via Stripe) introduces distributed state risks. Without strict idempotency, duplicate webhook delivery or concurrent user checkouts can lead to duplicate charges, duplicate enrollment rows, or unfulfilled orders.
3. **Operational Overengineering vs. Content Governance:** Many modern architectures prematurely decompose learning platforms into dozens of distributed microservices (separate auth, catalog, video, and payment services), introducing heavy network latency, deployment complexity, and eventual consistency failures. Conversely, open platforms without administrative verification often suffer from unvetted instructors and low-quality spam courses.

---

### The Solution: EdTech Platform
EdTech Platform addresses these challenges through a unified, high-integrity **Modular Monolith** that orchestrates the entire course lifecycle:

* **Authoritative Progress Engine:** Tracks student playback via periodic heartbeat pings and enforces server-side completion validation (requiring students to consume $\ge 95\%$ of lesson media duration before issuing completion status) while persisting exact resume coordinates.
* **Resilient Transaction & Webhook Processing:** Integrates Stripe Checkout with server-side webhook verification, idempotent payment handling, and database-level unique constraints (`UNIQUE(user_id, course_id)`) to eliminate checkout race conditions.
* **Two-Sided Trust & Moderation Pipeline:** Separates students, instructors, and admins with role-based access control. Instructors undergo identity and credential vetting by platform admins, and draft courses must pass administrative moderation checks before appearing in public catalog searches.
* **Cost-Effective, Resilient Architecture:** Consolidates authentication, catalog discovery, media asset management, quizzes, submissions, and payments into decoupled Spring modules running in a single deployable artifact—yielding immediate database consistency, sub-millisecond inter-module communication, and minimal operational overhead.

## Features and Capabilities

The platform implements complete end-to-end workflows tailored to three distinct personas: **Students**, **Instructors**, and **Platform Administrators**.

---

### 1. Student Experience & Learning Engine
* **Course Discovery & Trigram Search:**
    * Fuzzy search across course titles, descriptions, and categories powered by PostgreSQL `pg_trgm` indexes.
    * Multi-attribute filtering by category, difficulty level (Beginner, Intermediate, Advanced), price (Free vs. Paid), and rating.
    * Public syllabus preview displaying section modules and lesson outlines without exposing gated content.
* **Enrollment & Payments:**
    * Instant enrollment in free courses.
    * Hosted Stripe Checkout sessions for paid courses with automated receipt generation.
    * Duplicate enrollment prevention guaranteed at the database level.
* **Interactive Classroom & Media Player:**
    * Multimedia lesson viewer supporting video streams and rich text articles.
    * **Resume Playback:** Automatically persists `last_played_seconds` to allow seamless continuation across sessions and devices.
    * **Authoritative Progress Heartbeats:** Background heartbeat pings validate engagement and automatically mark a lesson completed once $\ge 95\%$ of media duration is viewed.
    * Course completion percentage dynamically computed across all mandatory sections.
* **Quizzes & Assignments:**
    * Multi-choice quizzes with automated instant scoring, passing grade validation, and detailed answer explanations.
    * Assignment submissions with text solutions and file attachments.
    * View instructor grades, rubric scores, and personalized feedback.
* **Course Reviews & Social Proof:**
    * Verified student reviews with 1–5 star ratings and written testimonials (restricted strictly to enrolled students).

---

### 2. Instructor Studio & Course Management
* **Curriculum Builder:**
    * Create, update, and organize hierarchical course structures (Courses $\rightarrow$ Sections $\rightarrow$ Lessons).
    * Support for multiple lesson types: Video, Article/Reading, Quiz, and Assignment.
    * Drag-and-drop ordering and section reindexing.
* **Media Management:**
    * Secure media uploading for video lessons and course thumbnails.
    * Dual storage engine: local filesystem storage for development, Backblaze B2 (S3-compatible API) for production.
    * Automatic MIME-type validation and file size restrictions (up to 500MB).
* **Assessment Creation & Grading:**
    * Quiz editor with customizable answer choices, single/multiple correct answers, and passing thresholds.
    * Assignment creation with submission deadlines and grading rubrics.
    * Grading portal to review submitted student work, assign numerical grades, and provide feedback notes.
* **Instructor Verification & Analytics:**
    * Instructor profile onboarding with submission of credentials and bios for administrative vetting.
    * Performance dashboard displaying total enrolled students, course ratings, and revenue metrics.

---

### 3. Platform Administration & Governance
* **Course Moderation Pipeline:**
    * Review submitted courses before they appear in public search.
    * Course lifecycle controls: Approve, Reject (with feedback notes), or Archive.
* **Instructor Verification Queue:**
    * Dedicated queue to audit instructor credentials and approve/reject instructor privileges.
* **User & Role Management:**
    * Administrative oversight to inspect user accounts, toggle active status, and audit access permissions.
* **Platform Health & Metrics:**
    * Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/info`) for container and database health monitoring.

---

### 4. Cross-Cutting Platform Capabilities
* **Role-Based Access Control (RBAC):** Strict stateless JWT authentication securing routes according to user role (`STUDENT`, `INSTRUCTOR`, `ADMIN`).
* **Decoupled Event Notifications:** Asynchronous domain events (e.g., `CoursePublishedEvent`, `EnrollmentCompletedEvent`, `AssignmentGradedEvent`) dispatch in-app notifications and email alerts without blocking HTTP request threads.
* **Configurable Email Provider:** Pluggable email dispatch supporting Resend in production and a mock provider for local development, backed by user notification preferences.

## Technology Stack

The platform is designed around a modern, reliable, and production-tested open-source stack. Every tool has a defined architectural role:

### 1. Core Backend & Application Framework
| Technology | Version | Architectural Role |
| :--- | :--- | :--- |
| **Java** | 21 (LTS) | Strongly typed runtime utilizing records for immutable DTOs, modern pattern matching, and LTS performance stability. |
| **Spring Boot** | 3.3.4 | Core enterprise application framework managing dependency injection, embedded Tomcat servlet container, and graceful application shutdown. |
| **Spring Data JPA / Hibernate** | 6.x | Object-relational mapping, declarative transaction management (`@Transactional`), and pagination support. |
| **Spring Security** | 6.x | Stateless HTTP filter chain, BCrypt password hashing, and granular Role-Based Access Control (RBAC) across endpoints. |
| **Spring Boot Actuator** | 3.3.4 | Operational observability, health probing (`/actuator/health`), and readiness checks for container orchestrators. |

---

### 2. Database & Schema Management
| Technology | Version | Architectural Role |
| :--- | :--- | :--- |
| **PostgreSQL** | 16 | Primary ACID-compliant relational data store. Employs `pg_trgm` extension for fuzzy catalog text searching and relational constraints to prevent race conditions. |
| **Flyway** | 10.x | Code-first database migrations (`V1` to `V16`) ensuring deterministic schema versioning and zero drift across development, CI, and production. |
| **HikariCP** | 5.x | High-throughput JDBC connection pooling with fine-tuned connection timeouts (20s) and lifecycle management. |

---

### 3. Security, Payments & Cloud Integrations
| Technology | Version | Architectural Role |
| :--- | :--- | :--- |
| **JJWT (Java JWT)** | 0.12.6 | Cryptographic creation, signing (HMAC-SHA256), and claims extraction for stateless Bearer authentication tokens. |
| **Stripe Java SDK** | 24.2.0 | Hosted Checkout session creation, payment intent verification, and cryptographically signed webhook consumption. |
| **AWS Java SDK (S3)** | 2.25.11 | Media storage abstraction targeting Backblaze B2 (S3-compatible) for production media persistence, with local filesystem fallback for offline dev. |
| **Resend & Mock Email** | API | Decoupled notification dispatch for transactional alerts (purchase receipts, account verification, grade alerts). |

---

### 4. Containerization, DevOps & Testing
| Technology | Tool / Framework | Architectural Role |
| :--- | :--- | :--- |
| **Docker & Docker Compose** | Engine 24+ | Multi-service local environment setup and reproducible production deployment orchestration. |
| **Nginx** | 1.25+ | Production reverse proxy managing Let's Encrypt TLS/HTTPS termination, static caching, and internal port forwarding to app port 8080. |
| **GitHub Actions** | CI Workflow | Automated build pipeline enforcing strict Maven compilation, linter passes, and automated test execution against ephemeral database containers. |
| **JUnit 5 & Mockito** | Test Framework | Service layer unit testing, mock-based isolation, and domain logic validation. |
| **Testcontainers** | 1.20.1 | Ephemeral PostgreSQL Docker container orchestration during integration tests to guarantee real-database parity. |

## 6. Architecture and Data Flow

### Architecture Overview

EduNest follows a **modular monolith architecture** built with Spring Boot. The backend runs as a single application while organizing business functionality into separate modules such as authentication, course management, enrollment, learning progress, assessments, payments, media, notifications, and administration.

The application uses a layered architecture to separate HTTP request handling, business logic, persistence, and external integrations. Controllers handle incoming REST requests, services enforce business rules and transactional operations, and Spring Data JPA repositories manage database access. PostgreSQL stores persistent application data, while Flyway manages version-controlled database migrations.

Cross-cutting components handle JWT authentication, role-based access control (RBAC), request correlation IDs, CORS configuration, and consistent exception handling. Spring application events connect modules when one business operation needs to trigger another module's behavior without directly coupling their repositories.

### Architecture Diagram

```mermaid
flowchart TD
    User([User: Student / Instructor / Admin]) --> UI[Frontend Client Application<br/>React / Vite / Mobile]

    UI -->|HTTPS / Bearer JWT| Filters[Pre-Processing & Security Filters<br/>RequestIdFilter & JwtAuthenticationFilter]

    subgraph SpringBoot["Spring Boot 3.3.4 Modular Monolith"]
        Filters --> Controllers[REST Controllers Layer<br/>Validation, DTOs & ApiResponse]

        subgraph DomainModules["Domain Modules & Core Services"]
            AuthMod["Auth & User Module<br/>(BCrypt, RBAC, Profiles)"]
            CourseMod["Course & Catalog Module<br/>(Curriculum, Reviews, Search)"]
            EnrollMod["Enrollment Module<br/>(Access Control, Ownership)"]
            ProgMod["Progress Engine<br/>(Heartbeats, 95% Threshold)"]
            AssessMod["Quiz & Assignment Module<br/>(Auto-Grading, Submissions)"]
            PayMod["Payment Module<br/>(Stripe Sessions, Idempotency)"]
            MediaMod["Media Storage Service<br/>(MIME Validation, S3 Client)"]
            AdminMod["Admin Moderation Module<br/>(Course Review, Verification)"]
        end

        Controllers --> DomainModules

        DomainModules -->|Publish Domain Events| EventBus[Spring ApplicationEventPublisher]
        EventBus -->|@TransactionalEventListener<br/>AFTER_COMMIT| NotifModule[Notification & Email Service]
    end

    DomainModules -->|Spring Data JPA / HikariCP| DB[(PostgreSQL 16 Database<br/>Flyway, pg_trgm, ACID Constraints)]

    PayMod <-->|API Sessions & Webhooks| Stripe[Stripe Payment Gateway]
    MediaMod -->|AWS S3 SDK| Storage[(Backblaze B2 / Local Filesystem)]
    NotifModule -->|HTTP REST| Resend[Resend Transactional Email API]
```

### Component Responsibilities

**Authentication and security** are handled through Spring Security and JWT-based authentication. The security filter validates bearer tokens and establishes the authenticated user's security context. Role-based authorization restricts operations according to user permissions, such as student, instructor, and administrator access.

**The controller layer** exposes REST APIs, validates incoming request data, invokes the appropriate service methods, and returns structured responses through DTOs. Keeping business rules out of controllers makes the API layer easier to maintain and test.

**The service layer** contains the platform's business logic. It coordinates operations such as course publication, enrollment, payment processing, progress tracking, and assessment grading. Transaction boundaries are defined at the service layer where related database operations must succeed or fail together.

**The persistence layer** uses Spring Data JPA and Hibernate to access PostgreSQL. Flyway applies versioned migrations to manage database schema changes. Database constraints, including the unique constraint on student-course enrollments, provide an additional layer of data integrity.

**Event-driven components** use Spring's application event mechanism to decouple business operations from notifications and email delivery. Transaction-bound listeners configured for `AFTER_COMMIT` execute only after the publishing transaction commits successfully. This prevents notifications from being triggered by transactions that roll back.

**External integrations** are isolated behind dedicated components. The payment module communicates with Stripe for checkout and payment confirmation, while the media module abstracts file storage between local development storage and Backblaze B2. Notification delivery uses Resend or a mock provider, depending on the environment.

### Data Flow

#### 1. User Registration and Authentication

When a user registers, the frontend sends a request to the authentication API. The authentication service checks whether the email is already registered and securely hashes the password using BCrypt before persisting the user in PostgreSQL. When an instructor registers, the platform also creates an instructor profile with a pending verification status.

During login, Spring Security authenticates the supplied credentials, and the application issues a signed JWT containing the relevant identity and authorization claims. For subsequent protected requests, the client sends the token in the `Authorization` header. The JWT filter validates the token and establishes the security context before the request reaches the controller. The controller delegates the operation to the service layer, which applies business rules and accesses the database through repositories.

#### 2. Course Creation, Review, and Publication

An instructor first completes the required verification process and creates a course in the `DRAFT` state. The instructor then builds the curriculum by adding sections and lessons and uploading any required media through the media storage service.

When the course is submitted for review, its status changes to `PENDING_REVIEW`. An administrator reviews the course and either approves or rejects it. Approval transitions the course to `PUBLISHED`, making it available through the course discovery APIs.

After a successful publication transaction, the application emits a course-related event. An after-commit listener handles the corresponding notification and email workflow. This separates course state changes from notification delivery while keeping the publishing operation within the course module.

#### 3. Stripe Payment and Student Enrollment

When a student purchases a course, the payment service first checks whether the student already has an active enrollment. It then creates a Stripe Checkout Session, associates the relevant user and course identifiers with the payment workflow, and stores the pending payment information.

After the student completes checkout, Stripe sends a webhook to the backend. The webhook handler verifies the signature using the configured webhook secret before accepting the event. The payment workflow uses Stripe event identifiers to recognize previously processed deliveries and avoid duplicate processing.

For a successful payment, the application updates the payment status and creates the student's enrollment. The database-level unique constraint on `(user_id, course_id)` helps prevent duplicate enrollment records, including when concurrent requests attempt to enroll the same student in the same course. Once the successful transaction commits, the application can publish a payment-success event for downstream notification handling.

### Key Design Decisions

**Modular monolith instead of microservices:** Related business modules run within a single application, allowing them to share the same deployment and use local method calls while retaining clear functional boundaries. This avoids the additional deployment, networking, and distributed-transaction complexity of splitting the platform into independent services prematurely.

**Transactional business operations:** Service-layer transactions and PostgreSQL constraints help preserve data consistency during related state changes. For example, enrollment uniqueness is enforced by the database rather than relying solely on application-level checks.

**Event-driven notifications:** Domain events separate core business operations from secondary actions such as notification creation and email delivery. After-commit listeners prevent transaction-dependent notifications from running before the corresponding database changes have committed.

**Database-managed schema evolution:** Flyway provides ordered, version-controlled migrations so that database changes can be tracked and applied consistently across development and deployment environments.