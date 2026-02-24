-- Postgres: allow DB to generate UUIDs if the app doesn't provide one
CREATE EXTENSION IF NOT EXISTS pgcrypto;

ALTER TABLE app_user
  ALTER COLUMN id SET DEFAULT gen_random_uuid();