<h1 align="center">amv</h1>
<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=spring-boot&logoColor=white">
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white">
  <img alt="Redis" src="https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/license-ISC-blue">
</p>

Spring Boot backend: auth (JWT), news CRUD, websocket.

## Stack

Java 17, Spring Boot 3.4.5, PostgreSQL, Redis, Spring Modulith

## Run

```bash
# with docker
docker-compose up

# without docker (needs postgres + redis running)
mvn spring-boot:run
```

Config through env vars or `application.properties`:

```
DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
REDIS_HOST, REDIS_PORT
JWT_EXPIRATION
```

## API

### Auth

```
POST /api/v1/public/auth/auth?username=x&password=x&email=x   — register
POST /api/v1/public/auth/login?username=x&password=x           — login
GET  /api/v1/public/auth/me                                    — current user (needs token)
```

### News (needs token except GET)

```
GET    /api/news?count=10   — list
POST   /api/news            — create
PUT    /api/news/{id}       — update
DELETE /api/news/{id}       — delete
```

### WebSocket

STOMP endpoint at `/ws`

## License

ISC
