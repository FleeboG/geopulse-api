ALTER TABLE event
  ADD COLUMN event_type TEXT NOT NULL DEFAULT 'OUTSIDE',
  ADD COLUMN matched_zone_names TEXT NOT NULL DEFAULT '';

CREATE INDEX IF NOT EXISTS idx_event_user_email_created_at
ON event(user_email, created_at DESC);