# Database — EdTech Platform

## Database Engine

PostgreSQL 16

## Migration Tool

Flyway — all schema changes are versioned migrations in `src/main/resources/db/migration/`.

## Naming Conventions

- Tables: `snake_case`, plural (e.g., `users`, `courses`, `enrollments`)
- Columns: `snake_case`
- Primary keys: `id` (UUID)
- Foreign keys: `<entity>_id`
- Timestamps: `created_at`, `updated_at`
- Indexes: `idx_<table>_<column(s)>`
- Unique constraints: `uq_<table>_<column(s)>`

## Extensions

| Extension | Purpose |
|-----------|---------|
| `uuid-ossp` | UUID generation |
| `pg_trgm` | Trigram similarity for future full-text search |

## Migrations

| Version | Description |
|---------|-------------|
| V1 | Baseline — extensions only |

*Tables will be documented here as phases complete.*

## Design Principles

- All primary keys are UUIDs (prevents ID enumeration attacks)
- All tables have `created_at` timestamps (minimum audit trail)
- Foreign key constraints are enforced at the database level
- Unique constraints enforce business uniqueness rules (e.g., one enrollment per user per course)
- Indexes are added based on actual query patterns, not preemptively
- Enums stored as `VARCHAR` with check constraints for flexibility
