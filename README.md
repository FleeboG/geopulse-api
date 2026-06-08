# geopulse-api

Backend API for GeoPulse, a geofencing/location pulse prototype.

## What it is

GeoPulse lets users register/login, create saved geofence zones, submit GPS events, and detect whether those events are `ENTER`, `INSIDE`, `EXIT`, or `OUTSIDE` relative to their zones.

## Tech stack

- Java 21
- Spring Boot 4
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker Compose
- Maven
- GitHub Actions

## How to run locally

```bash
docker compose -f docker-compose.local.yml up --build

# Repo structure

# Roadmap
