<h1 align="center">amv</h1>
<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-ED8B00?logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=spring-boot&logoColor=white">
  <img alt="PostgreSQL" src="https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white">
  <img alt="Redis" src="https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white">
  <img alt="MinIO" src="https://img.shields.io/badge/MinIO-C72E49?logo=minio&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/license-ISC-blue">
</p>

Anime viewing service. Catalog, video streaming from MinIO, user watchlists, **Watch Together** rooms, JWT auth.

## Stack

Java 17, Spring Boot 3.4.5, PostgreSQL, Redis, MinIO, WebSocket (STOMP)

## Run

```bash
docker-compose up
```

Or without docker (needs postgres + redis + minio running):

```bash
mvn spring-boot:run
```

Config via env vars:

```
DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
REDIS_HOST, REDIS_PORT
MINIO_ENDPOINT, MINIO_ACCESS_KEY, MINIO_SECRET_KEY
JWT_EXPIRATION
```

## API

### Auth

```
POST /api/v1/public/auth/auth?username=x&password=x&email=x   — register
POST /api/v1/public/auth/login?username=x&password=x           — login
GET  /api/v1/public/auth/me                                    — current user
```

### Anime (public read)

```
GET /api/v1/anime?page=0&size=20       — list
GET /api/v1/anime/{id}                 — details
GET /api/v1/anime/search?q=naruto      — search
```

### Episodes (public read)

```
GET /api/v1/episodes/anime/{animeId}   — list by anime
GET /api/v1/episodes/{id}              — metadata
```

### Streaming (public)

```
GET /api/v1/stream/video/{episodeId}       — video (supports Range)
GET /api/v1/stream/image/{animeId}/cover   — cover image
```

### Genres (public read)

```
GET /api/v1/genres         — list
GET /api/v1/genres/{id}    — details
```

### Admin (ROLE_ADMIN)

```
POST   /api/v1/admin/anime                       — create anime
PUT    /api/v1/admin/anime/{id}                   — update
DELETE /api/v1/admin/anime/{id}                   — delete
POST   /api/v1/admin/anime/{id}/cover             — upload cover image

POST   /api/v1/admin/episodes/anime/{animeId}     — create episode
POST   /api/v1/admin/episodes/{id}/video           — upload video
DELETE /api/v1/admin/episodes/{id}                 — delete episode

POST   /api/v1/admin/genres     — create genre
PUT    /api/v1/admin/genres/{id} — update
DELETE /api/v1/admin/genres/{id} — delete
```

### Watchlist (authenticated)

```
GET    /api/v1/watchlist                   — my watchlist
POST   /api/v1/watchlist/{animeId}         — add
DELETE /api/v1/watchlist/{animeId}         — remove
POST   /api/v1/watchlist/{animeId}/favorite — toggle favorite
GET    /api/v1/watchlist/favorites          — favorites only
```

### Watch Together (authenticated)

Create a room, share the 6-char code, watch an episode in sync with friends.

```
POST   /api/v1/rooms?episodeId=1   — create room (returns code)
GET    /api/v1/rooms/{code}        — room info (public)
DELETE /api/v1/rooms/{code}        — close room (host only)
```

Host controls playback, everyone sees it in real time.

### WebSocket

STOMP endpoint: `/ws`

**Watch Together** - subscribe to `/topic/room/{code}`, send sync events to `/app/room/{code}/sync`, chat to `/app/room/{code}/chat`:

```json
{"type": "PLAY",  "username": "user1", "timestamp": 42.5}
{"type": "PAUSE", "username": "user1", "timestamp": 67.2}
{"type": "SEEK",  "username": "user1", "timestamp": 120.0}
{"type": "CHAT",  "username": "user1", "message": "best scene"}
```

**New episodes** - subscribe to `/topic/new-episodes` for upload notifications.

## License

ISC
