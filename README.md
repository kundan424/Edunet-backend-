# EdTech Learning & Teaching Platform

A portfolio-grade EdTech platform inspired by Udemy/Coursera, built with Spring Boot as a modular monolith.

## Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.3.x |
| Security | Spring Security + JWT |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Build | Maven (Maven Wrapper) |
| Containers | Docker + Docker Compose |
| Testing | JUnit 5 + Testcontainers |

## Architecture

Modular monolith with clearly separated domain modules:

- **auth** — Registration, login, JWT
- **user** — User/instructor profiles
- **course** — Course & curriculum management
- **enrollment** — Course enrollment
- **progress** — Lesson/course progress tracking
- **quiz** — Quiz authoring and attempts
- **assignment** — Assignment submission and grading
- **payment** — Stripe payment integration
- **review** — Course reviews and ratings
- **media** — Video upload and processing
- **notification** — Event-driven notifications
- **admin** — Administrative operations
- **search** — Course discovery and search

See [ARCHITECTURE.md](ARCHITECTURE.md) for details.

## Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven (or use `./mvnw`)

## Local Setup

See [LOCAL_SETUP.md](LOCAL_SETUP.md) for full step-by-step instructions.

**Quick start:**
```bash
# 1. Start infrastructure
docker compose up -d

# 2. Copy environment file
cp .env.example .env
# Edit .env with your values

# 3. Run the application
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 4. Health check
curl http://localhost:8080/api/v1/health
```

## Running Tests

```bash
./mvnw test
```

Tests use Testcontainers — Docker must be running.

## API Documentation

See [API.md](API.md) (populated as phases complete).

## Project Status

| Phase | Status | Description |
|-------|--------|-------------|
| Phase 0 | ✅ Complete | Project foundation |
| Phase 1 | ⏳ Pending | Authentication & Users |
| Phase 2 | ⏳ Pending | Instructor Onboarding |
| Phase 3 | ⏳ Pending | Course Management |
| Phase 4 | ⏳ Pending | Course Discovery |
| Phase 5–20 | ⏳ Pending | See IMPLEMENTATION_PLAN.md |
