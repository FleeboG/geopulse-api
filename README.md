# geopulse-api

Backend API for GeoPulse, a geofencing/location pulse prototype.

## What it is

GeoPulse lets users register/login, create saved geofence zones, submit GPS events, and detect whether those events are `ENTER`, `INSIDE`, `EXIT`, or `OUTSIDE` relative to their zones.

## Tech stack

* Java 21
* Spring Boot 4
* Spring Security + JWT
* Spring Data JPA
* PostgreSQL
* Flyway
* Docker Compose
* Maven
* GitHub Actions

## How to run locally

Start the local API and PostgreSQL database:

```bash
docker compose -f docker-compose.local.yml up --build
```

Health check:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

## API smoke test

These commands use PowerShell.

### Register

```powershell
$body = @{
  email = "test@example.com"
  password = "password123"
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/register" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body
```

Expected:

```text
status
------
registered
```

### Login

```powershell
$login = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -ContentType "application/json" `
  -Body $body

$headers = @{ Authorization = "Bearer $($login.accessToken)" }
```

Expected: a JWT access token.

### Current user

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/me" `
  -Headers $headers
```

Expected:

```text
email
-----
test@example.com
```

### Create zone

```powershell
$zoneBody = @{
  name = "Home"
  latitude = 36.1867
  longitude = -94.1288
  radiusM = 150
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/zones" `
  -Method POST `
  -ContentType "application/json" `
  -Headers $headers `
  -Body $zoneBody
```

### List zones

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/zones" `
  -Headers $headers
```

### Create event inside zone

```powershell
$inside = @{
  latitude = 36.1867
  longitude = -94.1288
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/events" `
  -Method POST `
  -ContentType "application/json" `
  -Headers $headers `
  -Body $inside
```

First inside event should return:

```text
eventType : ENTER
```

Run the same inside event again. It should return:

```text
eventType : INSIDE
```

### Create event outside zone

```powershell
$outside = @{
  latitude = 35.0000
  longitude = -94.1288
} | ConvertTo-Json

Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/events" `
  -Method POST `
  -ContentType "application/json" `
  -Headers $headers `
  -Body $outside
```

Expected:

```text
eventType : EXIT
```

### Event history

```powershell
$response = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/events?limit=2" `
  -Headers $headers

$response.count
$response.limit
$response.items
```

Expected:

```text
2
2
```

## Repo structure

```text
src/main/java/com/geopulse/geopulse_api
├── auth
├── common
├── events
├── security
├── users
└── zones

src/main/resources
├── application.yaml
└── db/migration
```

## Roadmap

* Backend prototype: complete
* Android app: next
* AWS deployment
* Monitoring/observability
* Production hardening
