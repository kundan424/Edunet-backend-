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

**Planned**:
- User entity (id, email, passwordHash, role, status, timestamps)
- Flyway V2 migration
- Registration API
- Login API (returns JWT)
- JWT filter
- Current user profile API
- Role-based security rules
- Tests: unit (service), integration (API)
