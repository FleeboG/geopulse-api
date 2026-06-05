CREATE TABLE IF NOT EXISTS event (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_email   TEXT NOT NULL,

    latitude     DOUBLE PRECISION NOT NULL,
    longitude    DOUBLE PRECISION NOT NULL,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_event_user_email
ON event(user_email);

CREATE INDEX IF NOT EXISTS idx_event_created_at
ON event(created_at);