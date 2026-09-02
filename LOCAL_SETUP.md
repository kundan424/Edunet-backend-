# Local Development Setup

## Prerequisites

| Tool | Minimum Version | Notes |
|------|----------------|-------|
| Java | 21 | OpenJDK or GraalVM |
| Docker | 24+ | Docker Desktop on Mac/Windows |
| Docker Compose | v2 | Included with Docker Desktop |
| Maven | 3.9+ | Or use included `./mvnw` |

## Step 1: Clone the Repository

```bash
git clone <repository-url>
cd edtech-platform
```

## Step 2: Configure Environment

```bash
cp .env.example .env
```

Edit `.env` with your local values. The defaults work for local development with Docker Compose.

**Important**: Never commit `.env` to version control.

## Step 3: Start Infrastructure

```bash
docker compose up -d
```

This starts:
- PostgreSQL 16 on port 5432

Verify:
```bash
docker compose ps
```

## Step 4: Run the Application

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application starts on port 8080.

## Step 5: Verify

```bash
curl http://localhost:8080/api/v1/health
```

Expected response:
```json
{
  "success": true,
  "data": {
    "status": "UP",
    "service": "edtech-platform",
    "timestamp": "2024-01-01T00:00:00Z"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## Running Tests

Tests use Testcontainers — Docker must be running.

```bash
./mvnw test
```

For a specific test:
```bash
./mvnw test -Dtest=HealthControllerTest
```

## Stopping Infrastructure

```bash
docker compose down
```

To remove data volumes:
```bash
docker compose down -v
```

## IDE Setup

### IntelliJ IDEA
1. Open as Maven project
2. Set Project SDK to Java 21
3. Enable annotation processing (for Lombok)
4. Run configurations: use the `dev` profile

### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Enable Lombok support
