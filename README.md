# EdTech Platform Backend

Spring Boot 3.x and Java 21 modular monolith for an e-learning platform.

## overview
This is the backend API driving course descovery, enrollment, video media progress, quizzes, assignments, and Stripe payments. It employs JWT-based security with RBAC (Students, Instructors, Admins).

## Tech Stack
- **Core**: Java 21, Spring Boot 3.x
- **Database**: PostgreSQL with Flyway DB migrations
- **Auth**: JWT
- **Payments**: Stripe
- **Deployment**: Docker, {GitHub Actions CI}

## Deployment & Architecture
Please read our in-depth guides:
- [Deployment Guide](docs/deployment.md)
- [Architecture Decisions](docs/architecture.md)

## CI/CD Overview
We use GitHub Actions (.workflows/ci.yml) to enforce:
1. Strict Maven compilation
2. 100% Test pass rate against a live PostgreSQL test container
. Docker image build and push to GHCR on `main` branch.

## Known Limitations
 See `Architectures` documents. We intentionally skipped Kubernetes, Kafka, and external S3 storage for this phose to maintain a simple, reliable single-server deployment.
