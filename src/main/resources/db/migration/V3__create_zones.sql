CREATE TABLE IF NOT EXISTS zone (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_email  TEXT NOT NULL,
  name        TEXT NOT NULL,
  latitude    DOUBLE PRECISION NOT NULL,
  longitude   DOUBLE PRECISION NOT NULL,
  radius_m    DOUBLE PRECISION NOT NULL,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT zone_radius_positive CHECK (radius_m > 0)
);

CREATE INDEX IF NOT EXISTS idx_zone_user_email ON zone(user_email);