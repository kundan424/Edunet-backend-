# Architecture — EdTech Platform

## Architectural Style

The EdTech platform is implemented as a **modular monolith** using Spring Boot.

Instead of deploying many independent microservices from day one, the system groups related
business logic into clearly bounded modules within a single deployable unit.

### Why Modular Monolith?

- Simpler deployment and operational model
- Easier local development
- Full transactional consistency within a single database
- Allows architectural evolution: modules with independent scaling needs can later be extracted
- Avoids premature distributed systems complexity

## Module Boundaries

```
com.edtech.platform
├── common        # Shared infrastructure (exceptions, responses, security, utils)
├── auth          # Registration, login, JWT issuance
├── user          # User and instructor profiles
├── course        # Course CRUD, sections, lessons, lifecycle
├── enrollment    # Enrollment creation, access control
├── progress      # Video position, lesson/course completion
├── quiz          # Quiz authoring, attempts, grading
├── assignment    # Assignment authoring, submissions, grading
├── payment       # Stripe payment intents, webhooks
├── review        # Course reviews, ratings, aggregation
├── media         # Media upload, video processing, playback
├── notification  # Event-triggered notifications
├── admin         # Instructor verification, moderation
└── search        # Course discovery, filtering, pagination
```

## Request Flow

```
HTTP Request
     ↓
RequestIdFilter (correlation ID)
     ↓
Spring Security (JWT validation in Phase 1+)
     ↓
Controller (HTTP concerns, request DTO validation)
     ↓
Service Layer (business rules, authorization, transactions)
     ↓
Repository (persistence queries)
     ↓
PostgreSQL
```

## Module Extraction Strategy

Modules that may justify extraction as independent services:

| Module | Reason to Extract |
|--------|------------------|
| Payment | PCI compliance, independent scaling |
| Media Processing | CPU-intensive, independent scaling |
| Progress | High-frequency writes, independent scaling |
| Notification | Independent delivery, retries |
| Search | Elasticsearch integration |

Extraction is only justified when a clear scaling or organizational need exists.

## Key Integration Boundaries

- **Stripe**: External payment gateway. Webhook-driven state machine.
- **Object Storage (MinIO/S3)**: Blob storage for videos, thumbnails, assignments.
- **FFmpeg**: Video transcoding and HLS packaging (asynchronous).
- **Email Provider**: Notification delivery (abstracted behind an interface).

## Database Strategy

- Single PostgreSQL instance (Phase 0–Phase 18)
- Flyway for all schema migrations
- Read replicas can be introduced later if read load justifies it

## Security Model

- Stateless JWT authentication
- Role-based access control: STUDENT, INSTRUCTOR, ADMIN
- Object-level authorization: instructors own their courses, students own their progress
- Webhook signature verification for Stripe
