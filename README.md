# MetalPulse

A **production-grade precious metals trading platform** built with Spring Boot 4, React 19, and a modern event-driven backend stack. Users can register, fund their account, trade gold/silver/platinum/palladium at live market prices, and track portfolio performance with real-time analytics.

---

## Architecture Overview

```
React (Vite)  ──────►  REST API (Spring Boot)  ──────►  MySQL 8
                              │
                              ├──►  Redis 7           (spot-price cache + JWT blacklist)
                              │
                              ├──►  Kafka 3 (KRaft)   (trade event pipeline)
                              │         │
                              │         ├──► TradeAuditConsumer   (structured audit log)
                              │         └──► PortfolioConsumer    (live holdings in Redis)
                              │
                              ├──►  Goldbroker API     (live spot prices)
                              │         └── Resilience4j: @Retry(3x) + @CircuitBreaker
                              │
                              └──►  gRPC (port 9090)  (auth + trade internal services)
```

Every `BUY / SELL / HOLD` trade is committed to MySQL first, then a `TradeExecutedEvent` is published to Kafka via `@TransactionalEventListener(AFTER_COMMIT)` — guaranteeing no Kafka message is sent if the database transaction rolls back.

---

## Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Framework | Spring Boot 4 · Java 21 |
| API | Spring Web (REST) · gRPC (Protobuf) |
| Security | Spring Security · JWT (JJWT 0.12) |
| Persistence | Spring Data JPA · MySQL 8 · Flyway migrations |
| Caching | Redis 7 · Spring Cache (`@Cacheable`) |
| Messaging | Apache Kafka 3 (KRaft, no Zookeeper) · Spring Kafka |
| Resilience | Resilience4j — `@Retry` (exponential backoff) + `@CircuitBreaker` |
| Observability | Spring Boot Actuator · Micrometer · Prometheus |
| Docs | Springdoc OpenAPI / Swagger UI |
| Testing | JUnit 5 · Testcontainers · AssertJ · Mockito |

### Frontend
| Layer | Technology |
|---|---|
| Framework | React 19 · TypeScript · Vite 8 |
| Styling | Tailwind CSS 4 |
| State | TanStack Query v5 |
| HTTP | Axios |
| Charts | Recharts |
| Animation | Framer Motion |

---

## Prerequisites

- **Docker + Docker Compose** — for the recommended one-command setup
- **Java 21** — for running the backend locally without Docker
- **Node.js 18+** — for the frontend

---

## Running with Docker Compose (Recommended)

This starts MySQL, Redis, Kafka, and the Spring Boot app together with health checks ensuring the correct startup order.

### 1. Clone and configure

```bash
git clone https://github.com/Tattaisreeram/MetalPulse.git
cd MetalPulse
cp .env.example .env
```

Edit `.env` and set a strong JWT secret (minimum 32 characters):

```env
DATABASE_URL=jdbc:mysql://127.0.0.1:3306/metalpulse_db?...
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
JWT_SECRET=replace-with-a-strong-random-secret-min-32-chars
JWT_EXPIRATION_MS=86400000
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 2. Build the JAR

```bash
./gradlew :service:bootJar
```

### 3. Start everything

```bash
docker compose up
```

The app will be available at:

| Service | URL |
|---|---|
| REST API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| gRPC | `localhost:9090` |
| Actuator health | `http://localhost:8080/actuator/health` |
| Prometheus metrics | `http://localhost:8080/actuator/prometheus` |

To start only the infrastructure (and run the app locally):

```bash
docker compose up mysql redis kafka -d
```

---

## Running Locally (without Docker)

Requires MySQL 8, Redis 7, and optionally Kafka running locally.

### 1. Start infrastructure

```bash
# Redis
redis-server

# MySQL — create the database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS metalpulse_db;"

# Kafka (optional — app starts without it, trades still persist to DB)
# Download Kafka 3.x and run with KRaft mode, or use Docker just for Kafka:
docker compose up kafka -d
```

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env with your local MySQL credentials and JWT secret
```

### 3. Start the backend

```bash
./gradlew :service:bootRun
```

The backend starts on `http://localhost:8080`. On first run, Flyway automatically creates the `users` and `trades` tables.

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend runs on `http://localhost:5173` and proxies all `/api` requests to the backend.

---

## Running Tests

Integration tests use **Testcontainers** — Docker must be running. Real MySQL and Redis containers are spun up automatically; no manual setup required.

```bash
./gradlew :service:test
```

### What the tests cover

| Test class | What it proves |
|---|---|
| `SpotPriceCacheIT` | Cache-aside pattern — calling `getSpotPrice()` twice hits the Goldbroker API only once (second call is a Redis cache hit) |
| `TradeFlowIT` | Full trade persistence — `BUY` deducts the correct balance; `SELL` credits it; both persist the trade row to MySQL |

To run a single test class:

```bash
./gradlew :service:test --tests "com.smarthmalik.metalpulse.SpotPriceCacheIT"
./gradlew :service:test --tests "com.smarthmalik.metalpulse.TradeFlowIT"
```

---

## API Endpoints

Interactive documentation is at `http://localhost:8080/swagger-ui.html`. Key endpoints:

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/v1/auth/register` | Register a new user (includes a welcome bonus) |
| `POST` | `/api/v1/auth/login` | Log in and receive a JWT bearer token |
| `POST` | `/api/v1/auth/logout` | Invalidate the current token (Redis blacklist) |
| `GET` | `/api/v1/metals/spot-price` | Live spot price (Redis-cached, 60s TTL) |
| `GET` | `/api/v1/metals/historical` | Paginated historical prices |
| `GET` | `/api/v1/metals/full-history` | Full price history (Redis-cached, 1h TTL) |
| `POST` | `/api/v1/trades/buy` | Buy a metal |
| `POST` | `/api/v1/trades/sell` | Sell a metal |
| `POST` | `/api/v1/trades/hold` | Record a hold position |
| `POST` | `/api/v1/trades/deposit` | Deposit funds |
| `POST` | `/api/v1/trades/withdraw` | Withdraw funds |
| `GET` | `/api/v1/trades/balance` | Current account balance |
| `GET` | `/api/v1/trades/history` | Paginated trade history |
| `GET` | `/api/v1/analytics/price-change` | Price change analytics |
| `GET` | `/api/v1/analytics/returns` | Portfolio return calculations |

All endpoints except register and login require a `Authorization: Bearer <token>` header.

---

## Observability

| Endpoint | Description |
|---|---|
| `/actuator/health` | App health including DB, Redis, and circuit breaker state |
| `/actuator/metrics` | JVM, HTTP request, and cache metrics |
| `/actuator/prometheus` | Prometheus-format scrape endpoint (plug into Grafana) |
| `/actuator/circuitbreakers` | Live circuit breaker state for the Goldbroker API |

---

## Resilience

The Goldbroker spot price API is protected by a two-layer resilience strategy:

1. **`@Retry`** — retries up to 3 times with exponential backoff (`500ms → 1s → 2s`) before propagating the failure
2. **`@CircuitBreaker`** — opens after 50% failure rate over 10 calls; stays open for 30 seconds; half-opens with 3 probe calls

Circuit state is visible at `/actuator/health` and `/actuator/circuitbreakers`.

---

## Kafka Event Pipeline

Every `BUY`, `SELL`, and `HOLD` trade publishes a `TradeExecutedEvent` to the `trade-events` Kafka topic (3 partitions, keyed by `userId` for per-user ordering). Two independent consumer groups subscribe:

- **`audit-consumer-group`** — logs a structured audit record for every trade
- **`portfolio-consumer-group`** — maintains a live Redis hash of each user's metal holdings (`portfolio:{userId}` → `{metal: quantity}`)

The publish uses `@TransactionalEventListener(AFTER_COMMIT)`, ensuring no Kafka message is sent if the database transaction rolls back (avoids the dual-write problem).

---

## Project Structure

```
MetalPulse/
├── api/                  # Shared DTOs, exceptions, and event contracts
│   └── src/main/java/
│       └── com/smarthmalik/metalpulse/
│           ├── dto/           request + response records
│           ├── event/         TradeExecutedEvent (Kafka payload)
│           └── exception/     typed exceptions
├── service/              # Spring Boot application
│   └── src/
│       ├── main/java/
│       │   └── com/smarthmalik/metalpulse/
│       │       ├── configuration/   AppConfig, SecurityConfig, CacheConfig, KafkaConfig
│       │       └── core/
│       │           ├── controller/  REST endpoints
│       │           ├── facade/      orchestration layer
│       │           ├── service/     business logic
│       │           ├── kafka/       TradeEventRelay + consumers
│       │           ├── helper/      MetalPriceHelper (Goldbroker), FxRateHelper
│       │           ├── security/    JWT filter, token blacklist
│       │           ├── entity/      User, Trade
│       │           ├── repository/  JPA repositories
│       │           └── grpc/        AuthGrpcService, TradeGrpcService
│       ├── main/resources/
│       │   ├── application.properties
│       │   └── db/migration/        Flyway SQL migrations
│       └── test/java/               Testcontainers integration tests
├── proto/                # Protobuf definitions for gRPC
├── frontend/             # React + Vite + TypeScript
├── docker-compose.yml    # MySQL + Redis + Kafka + app
└── .env.example          # Environment variable template
```

---

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `DATABASE_URL` | Yes | JDBC URL for MySQL |
| `DATABASE_USERNAME` | Yes | MySQL username |
| `DATABASE_PASSWORD` | Yes | MySQL password |
| `JWT_SECRET` | Yes | Signing secret — minimum 32 characters |
| `JWT_EXPIRATION_MS` | No | Token TTL in ms (default: `86400000` = 24h) |
| `REDIS_HOST` | No | Redis host (default: `localhost`) |
| `REDIS_PORT` | No | Redis port (default: `6379`) |
| `KAFKA_BOOTSTRAP_SERVERS` | No | Kafka broker address (default: `localhost:9092`) |
