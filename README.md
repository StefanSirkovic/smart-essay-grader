# SmartEssayGrader

A full-stack essay grading platform that uses AI to provide automated feedback and scoring on student essays. Built with Spring Boot and React.

## What it does

Students submit essays through the web interface. The system sends them to Google's Gemini API for analysis, which returns a score and detailed feedback. Grading happens asynchronously through Kafka so the API stays responsive — students can check back for results or watch the status update in real time.

## Tech Stack

**Backend:**
- Java 21, Spring Boot 4.0.3
- Spring Security with JWT authentication
- Spring Data JPA + PostgreSQL
- Apache Kafka for async essay grading
- Redis for caching (essays, dashboard stats)
- Gemini API for AI-powered grading

**Frontend:**
- React 18 + TypeScript
- Tailwind CSS
- Axios with JWT interceptor
- React Router

**Infrastructure:**
- Docker & Docker Compose
- Kubernetes manifests for cluster deployment
- GitHub Actions CI pipeline

## Project Structure

```
src/main/java/com/stefan/essaygraderai/
├── config/          # Security, Kafka, Redis configuration
├── controller/      # REST endpoints (Auth, Essay, Dashboard)
├── dto/             # Request/response records
├── entity/          # JPA entities (User, Essay, Grade)
├── enums/           # EssayStatus, Role
├── exception/       # Custom exceptions + global handler
├── kafka/           # Producer and consumer for grading events
├── mapper/          # Entity-to-DTO mappers
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JwtService, UserDetailsService
└── service/         # Business logic (Auth, Essay, Grading, Dashboard, AI)

frontend/src/
├── api/             # Axios instance with auth interceptor
├── components/      # Layout, StatusBadge, ProtectedRoute
├── context/         # AuthContext (JWT state management)
├── pages/           # Login, Register, Dashboard, Essays, EssayDetail, CreateEssay
└── types/           # TypeScript interfaces matching backend DTOs
```

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login, returns JWT | No |
| POST | `/api/essays` | Submit an essay | Yes |
| GET | `/api/essays` | Get user's essays | Yes |
| GET | `/api/essays/{id}` | Get essay by ID | Yes |
| PUT | `/api/essays/{id}` | Update essay | Yes |
| DELETE | `/api/essays/{id}` | Delete essay | Yes |
| POST | `/api/essays/{id}/grade` | Trigger AI grading | Yes |
| GET | `/api/dashboard` | Get user stats | Yes |

## How Grading Works

1. User submits an essay via `POST /api/essays`
2. User triggers grading via `POST /api/essays/{id}/grade`
3. The request publishes a `GradingEvent` to Kafka
4. A Kafka consumer picks it up, calls the Gemini API
5. The AI response (score + feedback) is saved to the database
6. Essay status changes: `SUBMITTED` -> `GRADING` -> `GRADED` (or `FAILED`)
7. The frontend polls for status updates and displays results

## Running Locally

### Prerequisites
- Java 21
- Docker & Docker Compose
- Node.js 18+

### Steps

1. Clone the repo:
```bash
git clone https://github.com/StefanSirkovic/smart-essay-grader.git
cd smart-essay-grader
```

2. Create a `.env` file in the project root:
```
JWT_SECRET=your-secret-key-at-least-32-characters-long
AI_API_KEY=your-google-gemini-api-key
```

3. Start the infrastructure:
```bash
docker compose up db kafka redis -d
```

4. Run the backend:
```bash
export JWT_SECRET=your-secret-key-at-least-32-characters-long
export AI_API_KEY=your-google-gemini-api-key
./mvnw spring-boot:run
```

5. Run the frontend:
```bash
cd frontend
npm install
npm run dev
```

6. Open `http://localhost:5173`

### Running everything with Docker Compose

```bash
docker compose up --build
```

This builds the app image and starts all services. The app will be available at `http://localhost:8080`.

## Running Tests

```bash
./mvnw clean verify
```

Runs 15 tests total:
- **Unit tests:** AuthService (3), EssayService (5) — isolated with Mockito
- **Integration tests:** AuthController (5), EssayController (4) — full Spring context with MockMvc
- Tests use H2 in-memory database and mock Kafka/Redis beans

## Kubernetes

Manifests are in the `k8s/` directory. To deploy locally with Minikube:

```bash
minikube start
minikube image build -t smart-essay-app .
kubectl apply -f k8s/namespace.yml
kubectl apply -f k8s/secrets.yml   # edit with real values first
kubectl apply -f k8s/postgres.yml
kubectl apply -f k8s/redis.yml
kubectl apply -f k8s/kafka.yml
kubectl apply -f k8s/app.yml
minikube service smart-essay-app -n essay-grader
```
