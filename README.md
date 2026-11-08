# Kairos backend

This repository contains the first version of the Kairos backend and domain model.
It uses Java 21 records, Spring Boot 3, PostgreSQL, Liquibase and Maven.

## Current scope

- Immutable domain objects for job offers, applications, interview steps and received emails
- Proposed, applied or rejected application events
- Typed application changes and an event history model
- PostgreSQL persistence for applications and job offer snapshots
- An API endpoint for changing an application's status
- A Spring Boot application with a health endpoint

The domain sources are under `src/main/java/com/kairos/domain`.

## Requirements

- JDK 21
- Maven 3.9 or later

## Build and test

```bash
mvn test
```

## Run locally

```bash
mvn spring-boot:run
```

Once the application is running, check its health with:

```bash
curl http://localhost:8080/api/health
```

The endpoint returns:

```json
[
  {"source":"application","result":1},
  {"source":"database","result":1}
]
```

## Run with Docker Compose

Java and Maven do not need to be installed on the host when the application is
run in containers.

```bash
docker compose up --build
```

Then check the health endpoint:

```bash
curl http://localhost:8080/api/health
```

PostgreSQL is available on port `5432` with the development database, user and
password all named `kairos`. Its data is stored in the `postgres-data` volume.

The Compose configuration activates the `dev` Spring profile. On startup, an
idempotent seeder creates a sample user, company, offer, snapshot and application.
The sample application ID is:

```text
50000000-0000-0000-0000-000000000001
```

All seeded applications can be listed with:

```bash
curl http://localhost:8080/api/applications
```

It can be changed with:

```bash
curl -X PATCH http://localhost:8080/api/applications/50000000-0000-0000-0000-000000000001/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"ONGOING"}'
```

The seeded application also contains a scheduled step with this ID:

```text
60000000-0000-0000-0000-000000000001
```

It can be replaced with:

```bash
curl -X PUT \
  http://localhost:8080/api/applications/50000000-0000-0000-0000-000000000001/steps/60000000-0000-0000-0000-000000000001 \
  -H 'Content-Type: application/json' \
  -d '{
    "type": "INTERVIEW",
    "title": "HR interview",
    "position": 0,
    "objective": "Discuss the role and mutual expectations.",
    "format": "VIDEO_CALL",
    "scheduledAt": "2026-11-10T09:00:00Z",
    "participants": ["Alice Recruiter"],
    "practicalInformation": "Video link sent by email.",
    "preparationNotes": "Prepare a concise career overview.",
    "debrief": "Positive conversation.",
    "status": "COMPLETED"
  }'
```

A new step can be appended to the application pipeline with:

```bash
curl -X POST \
  http://localhost:8080/api/applications/50000000-0000-0000-0000-000000000001/steps \
  -H 'Content-Type: application/json' \
  -d '{
    "type": "TECHNICAL_TEST",
    "title": "Java exercise",
    "format": "TAKE_HOME",
    "status": "TO_SCHEDULE"
  }'
```

Stop the application with `Ctrl+C`, then remove its container with:

```bash
docker compose down
```
