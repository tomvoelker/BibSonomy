# Container Setup (New Stack)

This guide covers containerization for the modern stack only:
- REST API v2 (`bibsonomy-rest-api-v2`)
- Webapp v2 (`bibsonomy-webapp-v2`)

Elasticsearch is intentionally omitted for now.

## Variant A: Minimal local/test stack (DB + API + Webapp)

This variant spins up a MariaDB container and loads the legacy test data used by unit tests.
It is intended for local development and basic CI for the new stack only.

### Start

```bash
docker compose -f docker/docker-compose.yml up --build
```

### Endpoints

- Webapp: `http://localhost:5173`
- API: `http://localhost:8080/api/v2`
- MariaDB: `localhost:3306`

### Data initialization

The MariaDB container initializes from:
- `bibsonomy-database/src/main/resources/database/bibsonomy-db-schema.sql`
- `bibsonomy-database/src/main/resources/database/bibsonomy-db-init.sql`
- `bibsonomy-database/src/test/resources/database/insert-test-data.sql`

### Notes

- The database user is `bibsonomy` with password `password` (see `docker/mysql-init/00-create-dbs.sql`).
- The API uses `SPRING_PROFILES_ACTIVE=local` and maps `DATABASE_*` env vars into legacy `-Ddatabase.*` system properties at container startup.
- The webapp is built with `VITE_API_BASE_URL=http://localhost:8080/api/v2` and mocks disabled.
- On Apple Silicon, the REST API container runs as `linux/amd64` (set in `docker/docker-compose.yml`) to avoid JVM issues on arm64.

## Variant B: Kubernetes (API + Webapp, external DB clone)

This variant assumes a cloned production database (no DB container). Configure the API to point at the clone using the same property keys as `bibsonomy-rest-api-v2/src/main/resources/application-local.yml`:

- `DATABASE_MAIN_URL`
- `DATABASE_MAIN_USERNAME`
- `DATABASE_MAIN_PASSWORD`
- `DATABASE_MAIN_DRIVERCLASSNAME` (use `com.mysql.jdbc.Driver`)
- `DATABASE_RECOMMENDER_ITEM_URL`
- `DATABASE_RECOMMENDER_ITEM_USERNAME`
- `DATABASE_RECOMMENDER_ITEM_PASSWORD`
- `DATABASE_RECOMMENDER_TAG_URL`
- `DATABASE_RECOMMENDER_TAG_USERNAME`
- `DATABASE_RECOMMENDER_TAG_PASSWORD`

Build the webapp image with `VITE_API_BASE_URL` pointing to the API Service URL (or your ingress URL).

### Suggested env template (API)

```bash
SPRING_PROFILES_ACTIVE=local
DATABASE_MAIN_URL=jdbc:mysql://<db-clone-host>/bibsonomy?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull
DATABASE_MAIN_USERNAME=bibsonomy
DATABASE_MAIN_PASSWORD=***
DATABASE_MAIN_DRIVERCLASSNAME=com.mysql.jdbc.Driver
DATABASE_RECOMMENDER_ITEM_URL=jdbc:mysql://<db-clone-host>/bibsonomy_item_recommender?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull
DATABASE_RECOMMENDER_ITEM_USERNAME=bibsonomy
DATABASE_RECOMMENDER_ITEM_PASSWORD=***
DATABASE_RECOMMENDER_TAG_URL=jdbc:mysql://<db-clone-host>/bibsonomy_recommender?autoReconnect=true&useUnicode=true&characterEncoding=utf-8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull
DATABASE_RECOMMENDER_TAG_USERNAME=bibsonomy
DATABASE_RECOMMENDER_TAG_PASSWORD=***
```

## Images

- API image build: `docker/Dockerfile.rest-api-v2`
- Webapp image build: `docker/Dockerfile.webapp-v2`

Both build from source to keep CI and local usage aligned.
