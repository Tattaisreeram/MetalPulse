# MetalPulse

MetalPulse is a **precious metals portfolio and trading platform** built with a **Spring Boot + Gradle backend** and a **React + Vite frontend**. It allows users to authenticate, track metals markets, simulate trades, monitor portfolio performance, and review analytics.

## ✨ Features

- **Authentication** with JWT-based login and registration
- **Market dashboard** for current metal price insights
- **Trading flow** for buy/sell operations
- **Portfolio tracking** for holdings and balances
- **Analytics views** for performance snapshots
- **OpenAPI documentation** via Springdoc Swagger UI

## 🏗️ Project Structure

- `service/` — Spring Boot backend
- `api/` — shared DTOs and exception contracts
- `frontend/` — React UI

## 🧰 Prerequisites

- **Java 21**
- **Node.js 18+**
- **MySQL 8+** (or a MySQL-compatible database)
- **Gradle Wrapper** (included)

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Tattaisreeram/MetalPulse.git
cd MetalPulse
```

### 2. Configure the backend

Copy the example file and fill in your local values:

```bash
cp .env.example .env
```

Then edit `.env` and replace the placeholder values with your local MySQL credentials and a strong JWT secret.

```env
DATABASE_URL=jdbc:mysql://127.0.0.1:3306/metalpulse_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DATABASE_USERNAME=your_mysql_username
DATABASE_PASSWORD=your_mysql_password
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION_MS=86400000
```

The backend loads this file automatically through Spring Boot. Keep your real `.env` file local and do not commit it.

### 3. Start the backend

```bash
./gradlew :service:bootRun
```

The backend will run on `http://localhost:8080`.

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will run on `http://localhost:5173` and proxy `/api` requests to the backend.

## 📘 API Documentation

Once the backend is running, Swagger UI is available here:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/api-docs`

## 🔧 Useful Commands

### Backend

```bash
./gradlew :service:test
./gradlew :service:bootJar
```

### Frontend

```bash
cd frontend
npm run build
npm run lint
```

## 🔐 Environment Notes

- `JWT_SECRET` should be a strong, randomly generated secret in production.
- The default database credentials are intended for local development only.
- The frontend reads from `/api/v1` with a local proxy to `http://localhost:8080`.

## 🧪 Tech Stack

### Backend

- Spring Boot 4
- Spring Security
- Spring Data JPA
- MySQL Connector
- JJWT
- Springdoc OpenAPI

### Frontend

- React 19
- Vite 8
- React Router
- Axios
- React Query
- Recharts
- Lucide React

## 📝 Notes

The existing `frontend/README.md` is a Vite starter template and is not the project-level documentation. This root README is the main onboarding guide for the repository.
