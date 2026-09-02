# Architecture Decision Records — EdTech Platform

## ADR-001: Modular Monolith Instead of Microservices Initially

**Status**: Accepted

**Context**:
The system needs to be portfolio-quality and demonstrate real architectural thinking.
Microservices add significant operational complexity (service discovery, distributed tracing,
inter-service authentication, network failures, distributed transactions).

**Decision**:
Implement as a single Spring Boot application with clearly separated module packages.

**Consequences**:
- Simpler deployment and local development
- Full ACID transactions within the monolith
- Module boundaries enforced by package structure and code review
- Selected modules (Payment, Media, Progress, Notification, Search) are designed for future extraction

---

## ADR-002: PostgreSQL as Primary Database

**Status**: Accepted

**Context**:
Needs ACID transactions, foreign key integrity, rich query support, and proven reliability.

**Decision**:
Use PostgreSQL 16 as the sole relational database.

**Consequences**:
- Full transactional consistency
- Rich indexing including GIN indexes for future full-text search
- pg_trgm available for similarity search
- Single operational concern

---

## ADR-003: Database-Backed Search Before Elasticsearch

**Status**: Accepted

**Context**:
Elasticsearch adds significant operational complexity. The initial course catalog will fit
comfortably within PostgreSQL's search capabilities using ILIKE and pg_trgm.

**Decision**:
Implement course search using PostgreSQL. Define a `CourseSearchRepository` interface so that
the Elasticsearch implementation can be substituted later without changing business logic.

**Consequences**:
- No additional infrastructure in Phase 0–4
- Search performance sufficient for portfolio scale
- Clean abstraction allows Elasticsearch introduction in Phase 18 if justified

---

## ADR-004: S3-Compatible Object Storage for Media

**Status**: Accepted

**Context**:
Videos, thumbnails, and assignment files need durable, scalable blob storage.

**Decision**:
Use a `StorageService` interface backed by MinIO for local development. The interface can be
implemented with AWS S3 or another provider without changing the business logic.

**Consequences**:
- Local development uses MinIO via Docker Compose
- Production can use AWS S3 with a different implementation
- No vendor lock-in in the domain layer

---

## ADR-005: Asynchronous Video Processing

**Status**: Accepted

**Context**:
Video transcoding is CPU-intensive and long-running. Blocking the upload request for transcoding
would degrade the user experience and tie up server threads.

**Decision**:
Process videos asynchronously after upload completes. Use a media state machine:
`UPLOADING → PROCESSING → READY / FAILED`.
Notify the instructor when processing completes.

**Consequences**:
- Upload API is fast and non-blocking
- Processing can run in a background thread pool (Phase 11) or a dedicated worker (Phase 17)
- Instructors must wait for READY status before the lesson is published

---

## ADR-006: JWT for Stateless Authentication

**Status**: Accepted

**Context**:
The API is consumed by a frontend SPA and potentially mobile clients. Server-side sessions
require sticky sessions or a session store.

**Decision**:
Use JWT (HS256) for stateless authentication. Tokens are signed with a server-side secret
stored in environment variables. Token expiration is configurable.

**Consequences**:
- No session store required
- Token revocation requires a blocklist (to be addressed if needed)
- Secret must be rotated carefully
- Never hard-code the JWT secret

---

## ADR-007: Idempotency as First-Class Requirement

**Status**: Accepted

**Context**:
Network failures and retries can cause duplicate operations (double enrollment, double payment).

**Decision**:
Critical operations (enrollment, payment, webhook handling) must be idempotent.
Database unique constraints are the final safety net. Application-level checks are the first line.

**Consequences**:
- `UNIQUE(user_id, course_id)` on enrollments at DB level
- Stripe webhook events are checked for prior processing
- Enrollment creation is safe to retry
