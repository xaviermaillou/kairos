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
{"status":"ok"}
```
