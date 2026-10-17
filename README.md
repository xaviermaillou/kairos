# Kairos backend

This repository contains the first version of the Kairos backend and domain model.
It uses Java 21 records, Spring Boot 3 and Maven. Persistence and the business API
have not been implemented yet.

## Current scope

- Immutable domain objects for job offers, applications, interview steps and received emails
- User-approved update suggestions produced from email analysis
- Typed application changes and an event history model
- A Spring Boot application with a health endpoint

The domain sources are under `src/main/java/io/kairos/domain`.

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
curl http://localhost:8080/health
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
curl http://localhost:8080/health
```

PostgreSQL is available on port `5432` with the development database, user and
password all named `kairos`. Its data is stored in the `postgres-data` volume.

Stop the application with `Ctrl+C`, then remove its container with:

```bash
docker compose down
```
