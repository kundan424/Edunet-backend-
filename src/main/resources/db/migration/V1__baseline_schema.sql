-- EdTech Platform Database Schema
-- Phase 0: Baseline migration
-- Tables will be added in subsequent phases.
-- This migration establishes the Flyway baseline.

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Enable pg_trgm for future full-text search optimization
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
